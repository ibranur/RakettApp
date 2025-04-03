import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository
import no.uio.ifi.in2000.team6.rakett_app.model.safetyreport.SafetyReport
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.DateWeatherInfo
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.GoodTimeWindow
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Calendar
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team6.rakett_app.data.CoordinatesManager
import no.uio.ifi.in2000.team6.rakett_app.data.repository.HourlyWeatherData
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
data class StartScreenUiState(
    val fromDate: LocalDate? = null,
    val toDate: LocalDate? = null,
    val errorMessage: String? = null,
    val weatherData: List<DateWeatherInfo> = emptyList(),
    val error: String? = null
) {
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    // Use computed property instead of function with the same name as the automatic getter
    val selectedDateRangeText: String
        get() = if (fromDate != null && toDate != null && errorMessage == null) {
            "Valgt periode: ${fromDate.format(dateFormatter)} - ${toDate.format(dateFormatter)}"
        } else {
            ""
        }
}

@RequiresApi(Build.VERSION_CODES.O)
class StartScreenViewModel(
    private val repository: SafetyReportRepository
) : ViewModel() {

    private val reportCache = mutableMapOf<LocalDate, SafetyReport>()
    private val _savedCoordinates = MutableStateFlow<Pair<Double, Double>>(59.9139 to 10.7522) // Default to Oslo

    private val _uiState = MutableStateFlow(StartScreenUiState())
    val uiState: StateFlow<StartScreenUiState> = _uiState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Observe location changes
        viewModelScope.launch {
            CoordinatesManager.currentLocation.collect { (lat, lon) ->
                updateCoordinates(lat, lon)
            }
        }
    }

    // Function for selecting "From" date
    fun onFromDateSelected(dateMillis: Long?) {
        dateMillis?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val newFromDate = LocalDate.of(year, month, day)

            validateDates(newFromDate, _uiState.value.toDate)

            // If both dates are valid, fetch data
            if (newFromDate != null && _uiState.value.toDate != null && _uiState.value.errorMessage == null) {
                fetchSafetyReports(newFromDate, _uiState.value.toDate!!)
            }
        }
    }

    fun updateCoordinates(lat: Double, lon: Double) {
        _savedCoordinates.value = lat to lon
        reportCache.clear() // Clear cache when location changes

        // Refresh data if dates are already selected
        val currentState = _uiState.value
        if (currentState.fromDate != null && currentState.toDate != null && currentState.errorMessage == null) {
            fetchSafetyReports(currentState.fromDate, currentState.toDate)
        }
    }

    // Function for selecting "To" date
    fun onToDateSelected(dateMillis: Long?) {
        dateMillis?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val newToDate = LocalDate.of(year, month, day)

            validateDates(_uiState.value.fromDate, newToDate)

            // If both dates are valid, fetch data
            if (_uiState.value.fromDate != null && newToDate != null && _uiState.value.errorMessage == null) {
                fetchSafetyReports(_uiState.value.fromDate!!, newToDate)
            }
        }
    }

    private fun validateDates(fromDate: LocalDate?, toDate: LocalDate?) {
        val errorMessage = when {
            fromDate != null && toDate != null && fromDate.isAfter(toDate) -> "Til-dato kan ikke være før fra-dato"
            else -> null
        }

        _uiState.value = _uiState.value.copy(
            fromDate = fromDate,
            toDate = toDate,
            errorMessage = errorMessage
        )
    }

    private fun fetchSafetyReports(fromDate: LocalDate, toDate: LocalDate) {
        // Don't fetch if we're already loading
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Get all dates in the range
                val dates = generateDateRange(fromDate, toDate)
                val coordinates = _savedCoordinates.value

                val weatherData = dates.map { date ->
                    // Get hourly data for this date
                    val hourlyData = repository.getHourlyForecastForDay(
                        coordinates.first, coordinates.second, date
                    )

                    // Find best time windows for this day
                    val goodTimeWindows = findBestLaunchWindows(hourlyData)

                    // Calculate day score based on best available hours
                    val dayScore = calculateDayScore(hourlyData)

                    DateWeatherInfo(
                        date = date.format(DateTimeFormatter.ofPattern("EEEE dd. MMMM", Locale.getDefault())),
                        weatherScore = dayScore,
                        goodTimeWindows = goodTimeWindows
                    )
                }

                _uiState.value = _uiState.value.copy(
                    weatherData = weatherData,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Kunne ikke hente værdata: ${e.message}",
                    weatherData = emptyList()
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Find the best time windows for launches
    private fun findBestLaunchWindows(hourlyData: List<HourlyWeatherData>): List<GoodTimeWindow> {
        // Only consider daytime hours (6 AM to 8 PM)
        val daytimeHours = hourlyData.filter { it.hourOfDay in 6..20 }

        // Score each hour for launch suitability
        val scoredHours = daytimeHours.map { hour ->
            val score = calculateLaunchScore(hour)
            ScoredHour(hour, score)
        }

        // Get the top 3 best hours
        val bestHours = scoredHours
            .sortedByDescending { it.score }
            .take(3)
            .filter { it.score > 6.0 } // Only include good conditions

        // Convert to time windows
        return bestHours.map { scoredHour ->
            val hour = scoredHour.data
            GoodTimeWindow(
                time = String.format("%02d:00-%02d:00", hour.hourOfDay, hour.hourOfDay + 1),
                details = "Wind: ${hour.windSpeed} m/s, " +
                        "Temp: ${hour.airTemperature}°C, " +
                        "Cloud: ${hour.cloudCover}%, " +
                        "Precip: ${hour.precipitation} mm, " +
                        "Humid: ${hour.humidity}%, " +
                        "Thunder: ${hour.thunderProbability}%"
            )
        }
    }

    private fun calculateLaunchScore(hour: HourlyWeatherData): Double {
        var score = 10.0 // Start with perfect score

        // Wind conditions (most critical)
        score -= when {
            hour.windSpeed > 10.0 -> 8.0
            hour.windSpeed > 8.0 -> 6.0
            hour.windSpeed > 6.0 -> 4.0
            hour.windSpeed > 4.0 -> 2.0
            hour.windSpeed > 2.0 -> 0.5
            else -> 0.0
        }

        // Precipitation
        score -= when {
            hour.precipitation > 2.0 -> 4.0
            hour.precipitation > 0.5 -> 2.0
            hour.precipitation > 0.0 -> 1.0
            else -> 0.0
        }

        // Cloud cover
        score -= hour.cloudCover / 25.0 // 0-4 point reduction

        // Thunder probability (critical for rockets)
        score -= hour.thunderProbability / 10.0 // 0-10 point reduction

        // Temperature effects
        if (hour.airTemperature < 0 || hour.airTemperature > 30) {
            score -= 1.0
        }

        // Wind gust penalties
        if (hour.windGust > hour.windSpeed * 1.5) {
            score -= 2.0
        }

        return score.coerceIn(0.0, 10.0)
    }

    private fun calculateDayScore(hourlyData: List<HourlyWeatherData>): Double {
        val daytimeHours = hourlyData.filter { it.hourOfDay in 6..20 }
        if (daytimeHours.isEmpty()) return 0.0

        // Average of the 3 best hours
        val bestScores = daytimeHours
            .map { calculateLaunchScore(it) }
            .sortedDescending()
            .take(3)

        return if (bestScores.isEmpty()) 0.0 else bestScores.average()
    }

    data class ScoredHour(val data: HourlyWeatherData, val score: Double)

    // Helper method to get saved coordinates
    private fun getSavedCoordinates(): Pair<Double, Double>? {
        // Implement this to get the user's saved coordinates
        // For example, you could inject a repository that stores coordinates
        return null // Replace with actual implementation
    }

    private fun generateDateRange(fromDate: LocalDate, toDate: LocalDate): List<LocalDate> {
        val dateList = mutableListOf<LocalDate>()
        var currentDate = fromDate

        while (!currentDate.isAfter(toDate)) {
            dateList.add(currentDate)
            currentDate = currentDate.plusDays(1)
        }

        return dateList
    }

    private fun calculateWeatherScore(report: SafetyReport): Double {
        // Simple weather score calculation - you can make this more sophisticated
        return when {
            report.wind_speed > 10 -> 2.0
            report.wind_speed > 5 -> 5.0
            report.wind_speed <= 5 -> 8.0
            else -> 5.0
        }
    }

    private fun generateGoodTimeWindows(report: SafetyReport): List<GoodTimeWindow> {
        val windows = mutableListOf<GoodTimeWindow>()

        // Early morning (6-8am)
        if (report.wind_speed < 3 && report.air_temperature > 5) {
            windows.add(GoodTimeWindow(
                time = "06:00-08:00",
                details = "Wind: ${report.wind_speed} m/s, Temp: ${report.air_temperature}°C, " +
                        "Cloud: ${report.cloud_area_fraction}%, Humid: ${report.relative_humidity}%"
            ))
        }

        // Morning (8-10am)
        if (report.wind_speed < 5) {
            windows.add(GoodTimeWindow(
                time = "08:00-10:00",
                details = "Wind: ${report.wind_speed} m/s, Temp: ${report.air_temperature}°C, " +
                        "Cloud: ${report.cloud_area_fraction}%, Precip: 0 mm"
            ))
        }

        if (report.wind_speed < 6 &&
            report.cloud_area_fraction < 50 &&
            report.air_temperature > 10 &&
            report.relative_humidity < 80) {

            windows.add(GoodTimeWindow(
                time = "12:00-14:00",
                details = "Wind: ${report.wind_speed} m/s, Temp: ${report.air_temperature}°C, " +
                        "Cloud: ${report.cloud_area_fraction}%, Humid: ${report.relative_humidity}%"
            ))
        }

        return windows
    }
}
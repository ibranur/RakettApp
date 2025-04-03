package no.uio.ifi.in2000.team6.rakett_app.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team6.rakett_app.LaunchPointState
import no.uio.ifi.in2000.team6.rakett_app.data.repository.LocationForecastRepository
import no.uio.ifi.in2000.team6.rakett_app.data.CoordinatesManager
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FiveDayUIState
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHourUIState

class HomeScreenViewModel(
    private val repository: SafetyReportRepository
) : ViewModel() {


    private val _temperatureState = MutableStateFlow(0.0)
    val temperatureState: StateFlow<Double> = _temperatureState.asStateFlow()

    private val _windSpeedState = MutableStateFlow(0.0)
    val windSpeedState: StateFlow<Double> = _windSpeedState.asStateFlow()

    private val _windDirectionState = MutableStateFlow(0.0)
    val windDirectionState: StateFlow<Double> = _windDirectionState.asStateFlow()

    private val _savedCoordinates = MutableStateFlow<List<Pair<Double, Double>>>(emptyList())
    val savedCoordinates: StateFlow<List<Pair<Double, Double>>> = _savedCoordinates.asStateFlow()


    private val _locationForecastRepository = LocationForecastRepository()
    private val _fiveDayUIState = MutableStateFlow(FiveDayUIState())
    val fiveDayUIState =  _fiveDayUIState.asStateFlow()

    private val _fourHourUIState = MutableStateFlow(FourHourUIState())
    val fourHourUIState =  _fourHourUIState.asStateFlow()



    private val _launchPointState = MutableStateFlow(LaunchPointState())
    val launchPointState = _launchPointState.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFiveDayForecast(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val fiveDayForecast = _locationForecastRepository.getFiveDayForecast(lat,lon)

                _fiveDayUIState.update {
                    it.copy(
                        forecast = fiveDayForecast)
                }
        }
    }

    fun updateSelectedLocation(state: LaunchPointState) {
        viewModelScope.launch(Dispatchers.IO) {

            _launchPointState.update {
                it.copy(
                    launchPoints = state.launchPoints
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFourHourForecast(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val fourHourForecast = _locationForecastRepository.getNextFourHourForecast(latitude,longitude)

            _fourHourUIState.update {
                it.copy(
                    list = fourHourForecast
                )
            }
        }
    }

    fun fetchWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val report = repository.getSafetyReport(lat, lon)
                _temperatureState.value = report.air_temperature
                _windSpeedState.value = report.wind_speed
                _windDirectionState.value = report.wind_from_direction
            } catch (e: Exception) {
                println("ViewModel Fetch Error: ${e.message}")
                e.printStackTrace()
                // default values if fetch fails
                _temperatureState.value = 0.0
                _windSpeedState.value = 0.0
                _windDirectionState.value = 0.0
            }
        }
    }

    fun saveCoordinates(lat: Double, lon: Double) {
        val currentList = _savedCoordinates.value.toMutableList()
        if (!currentList.contains(lat to lon)) {
            currentList.add(lat to lon)
            _savedCoordinates.value = currentList

            // Update shared coordinates
            CoordinatesManager.updateLocation(lat, lon)

            // Fetch weather data with new coordinates
            fetchWeatherData(lat, lon)
        }
    }
}

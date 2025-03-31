package no.uio.ifi.in2000.team6.rakett_app.data.repository

import no.uio.ifi.in2000.team6.rakett_app.data.LocationForecastDatasource
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Details
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Forecast

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class LocationForecastRepository {
    private val _locationForecastDatasource = LocationForecastDatasource()

    suspend fun getForecastTimeInstant(lat: Double, longitude: Double): Details {
        val forecast: Forecast? = _locationForecastDatasource.fetchForecast(lat, longitude)
        return forecast!!.properties.timeseries[0].data.instant.details
    }

    // New method to get all hourly data points for a specific day without timezone issues
    suspend fun getHourlyDetailsForDay(lat: Double, lon: Double, date: LocalDate): List<HourlyDetails> {
        val forecast = _locationForecastDatasource.fetchForecast(lat, lon)
            ?: throw Exception("Could not fetch forecast data")

        val dateString = date.format(DateTimeFormatter.ISO_DATE)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")

        return forecast.properties.timeseries
            .filter { timeEntry ->
                // Simple string comparison to avoid timezone issues
                timeEntry.time.startsWith(dateString)
            }
            .map { timeEntry ->
                val hour = timeEntry.time.substring(11, 13).toInt()
                HourlyDetails(
                    hour = hour,
                    details = timeEntry.data.instant.details,
                    precipDetails = timeEntry.data.next_1_hours?.details,
                    symbolCode = timeEntry.data.next_1_hours?.summary?.symbol_code ?: ""
                )
            }
            .sortedBy { it.hour }
    }

    // Keep the original method for backward compatibility
    suspend fun getForecast(lat: Double, longitude: Double): Forecast {
        return _locationForecastDatasource.fetchForecast(lat, longitude)
            ?: throw Exception("Could not fetch forecast data")
    }
}

data class HourlyDetails(
    val hour: Int,
    val details: Details,
    val precipDetails: no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.DetailsX?,
    val symbolCode: String
)
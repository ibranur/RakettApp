package no.uio.ifi.in2000.team6.rakett_app.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team6.rakett_app.data.LocationForecastDatasource
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast.DetailsInstant
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast.Forecast
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FiveDay
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHour
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.HourlyDay
import no.uio.ifi.in2000.team6.rakett_app.utils.fiveDaysFunction
import no.uio.ifi.in2000.team6.rakett_app.utils.nextFourHours
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class LocationForecastRepository {
    private val _locationForecastDatasource = LocationForecastDatasource()

    suspend fun getForecastTimeInstant(lat: Double, longitude: Double): DetailsInstant {
        val forecast: Forecast? = _locationForecastDatasource.fetchForecast(lat, longitude)
        return forecast!!.properties.timeseries[0].data.instant.details
    }

    // New method to get all hourly data points for a specific day without timezone issues
    suspend fun getHourlyDetailsForDay(lat: Double, lon: Double, date: LocalDate): List<HourlyDay> {
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
                HourlyDay(
                    time = hour,
                    air_temperature = 0.0,
                    wind_speed = 0.0,
                    wind_direction = 0.0,
                    precipitation_amount_one_hour = 0.0,
                    symbol_code = "ha",
                    details = timeEntry.data.instant.details,
                    precipDetails = timeEntry.data.next_1_hours?.details,
                    symbolCode = timeEntry.data.next_1_hours?.summary?.symbol_code ?: ""
                )

            }
            .sortedBy { it.time }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getNextFourHourForecast(latitude: Double, longitude: Double): List<FourHour> {
        val forecast: Forecast? = _locationForecastDatasource.fetchForecast(latitude, longitude)

        if (forecast == null) return emptyList()

        return nextFourHours(forecast)
    }

    // Keep the original method for backward compatibility
    suspend fun getForecast(lat: Double, longitude: Double): Forecast {
        return _locationForecastDatasource.fetchForecast(lat, longitude)
            ?: throw Exception("Could not fetch forecast data")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getFiveDayForecast(lat: Double, longitude: Double): List<FiveDay?> {
        val forecast: Forecast? = _locationForecastDatasource.fetchForecast(lat, longitude)

        if (forecast == null) return emptyList()

        return fiveDaysFunction(forecast)
    }


}

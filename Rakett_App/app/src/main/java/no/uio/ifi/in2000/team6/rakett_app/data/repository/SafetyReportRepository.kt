package no.uio.ifi.in2000.team6.rakett_app.data.repository


import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast.Timesery
import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap
import no.uio.ifi.in2000.team6.rakett_app.model.safetyreport.SafetyReport
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast.Forecast
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

class SafetyReportRepository {
    private val locationForecastRepository = LocationForecastRepository()

    // Existing method for backward compatibility
    suspend fun getSafetyReport(lat: Double, longitude: Double): SafetyReport {
        val forecastData = locationForecastRepository.getForecastTimeInstant(lat, longitude)
        return SafetyReport(
            air_pressure_at_sea_level = forecastData.air_pressure_at_sea_level ?: 0.0,
            air_temperature = forecastData.air_temperature,
            cloud_area_fraction = forecastData.cloud_area_fraction ?: 0.0,
            cloud_area_fraction_high = forecastData.cloud_area_fraction_high ?: 0.0,
            cloud_area_fraction_low = forecastData.cloud_area_fraction_low ?: 0.0,
            cloud_area_fraction_medium = forecastData.cloud_area_fraction_medium ?: 0.0,
            dew_point_temperature = forecastData.dew_point_temperature ?: 0.0,
            fog_area_fraction = forecastData.fog_area_fraction ?: 0.0,
            relative_humidity = forecastData.relative_humidity ?: 0.0,
            wind_from_direction = forecastData.wind_from_direction,
            wind_speed = forecastData.wind_speed,
            wind_speed_of_gust = forecastData.wind_speed_of_gust ?: 0.0,
            wind_sheare = listOf(0.0),
            wind_in_air = GribMap(0.0, 0.0, 0.0),
            score = 0.0
        )
    }

    // New method to get hourly data for a date range
    suspend fun getHourlyForecastForDay(lat: Double, lon: Double, date: LocalDate): List<HourlyWeatherData> {
        val hourlyDetails = locationForecastRepository.getHourlyDetailsForDay(lat, lon, date)
        return hourlyDetails.map { hourDetails ->
            HourlyWeatherData(
                time = "${hourDetails.time}:00",
                hourOfDay = hourDetails.time,
                airTemperature = hourDetails.details.air_temperature,
                windSpeed = hourDetails.details.wind_speed,
                windGust = hourDetails.details.wind_speed_of_gust ?: 0.0,
                windDirection = hourDetails.details.wind_from_direction,
                cloudCover = hourDetails.details.cloud_area_fraction ?: 0.0,
                precipitation = hourDetails.precipDetails?.precipitation_amount ?: 0.0,
                humidity = hourDetails.details.relative_humidity ?: 0.0,
                thunderProbability = hourDetails.precipDetails?.probability_of_thunder ?: 0.0
            )
        }
    }

    private fun extractHourlyData(forecast: Forecast, date: LocalDate): List<HourlyWeatherData> {
        return forecast.properties.timeseries
            .filter { entry ->
                val entryDate = LocalDateTime.ofInstant(
                    Instant.parse(entry.time),
                    ZoneId.systemDefault()
                ).toLocalDate()
                entryDate == date
            }
            .map { convertToHourlyData(it) }
    }

    private fun convertToHourlyData(entry: Timesery): HourlyWeatherData {
        val instant = entry.data.instant.details
        val next1h = entry.data.next_1_hours?.details

        return HourlyWeatherData(
            time = entry.time,
            hourOfDay = LocalDateTime.ofInstant(
                Instant.parse(entry.time),
                ZoneId.systemDefault()
            ).hour,
            airTemperature = instant.air_temperature,
            windSpeed = instant.wind_speed,
            windGust = instant.wind_speed_of_gust ?: 0.0,
            windDirection = instant.wind_from_direction,
            cloudCover = instant.cloud_area_fraction ?: 0.0,
            precipitation = next1h?.precipitation_amount ?: 0.0,
            humidity = instant.relative_humidity ?: 0.0,
            thunderProbability = next1h?.probability_of_thunder ?: 0.0
        )
    }
}

data class HourlyWeatherData(
    val time: String,
    val hourOfDay: Int,
    val airTemperature: Double,
    val windSpeed: Double,
    val windGust: Double,
    val windDirection: Double,
    val cloudCover: Double,
    val precipitation: Double,
    val humidity: Double,
    val thunderProbability: Double
)
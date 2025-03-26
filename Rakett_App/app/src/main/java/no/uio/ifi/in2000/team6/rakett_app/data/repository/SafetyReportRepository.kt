package no.uio.ifi.in2000.team6.rakett_app.data.repository

import no.uio.ifi.in2000.team6.rakett_app.model.SafetyReport
import no.uio.ifi.in2000.team6.rakett_app.data.repository.GribRepository
import kotlin.math.atan2
import kotlin.math.sqrt

class SafetyReportRepository {
    private val forecast = LocationForecastRepository()
    private val gribRepository = GribRepository()

    suspend fun getSafetyReport(lat: Double, longitude: Double): SafetyReport {
        val forecastData = forecast.getForecastTimeInstant(lat, longitude)


        val windSpeed = forecastData.wind_speed
        val windDirection = forecastData.wind_from_direction

        return SafetyReport(
            air_temperature = forecastData.air_temperature,
            wind_speed = windSpeed,
            wind_direction = windDirection
        )
    }


}
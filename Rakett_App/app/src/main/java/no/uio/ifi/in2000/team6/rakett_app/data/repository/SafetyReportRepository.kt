package no.uio.ifi.in2000.team6.rakett_app.data.repository

import no.uio.ifi.in2000.team6.rakett_app.model.SafetyReport

class SafetyReportRepository {
    private val forecast = LocationForecastRepository()

    suspend fun getSafetyReport(lat: Double, longitude: Double): SafetyReport {
        val forecastData = forecast.getForecastTimeInstant(lat,longitude)
        return SafetyReport(forecastData.air_temperature)
    }

}
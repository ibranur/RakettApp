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
        val gribData = gribRepository.getGribData(lat, longitude)

        // Hent u og v komponenter fra GRIB-data
        val uComponent = gribData?.ranges?.wind_speed?.values?.getOrNull(0) ?: 0.0
        val vComponent = gribData?.ranges?.wind_speed?.values?.getOrNull(1) ?: 0.0

        val windSpeed = CalculationRepository.calculateWindSpeed(uComponent, vComponent)
        val windDirection = CalculationRepository.calculateWindDirection(uComponent, vComponent)

        return SafetyReport(
            air_temperature = forecastData.air_temperature,
            wind_speed = windSpeed,
            wind_direction = windDirection
        )
    }


}
package no.uio.ifi.in2000.team6.rakett_app.data.repository

import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap
import no.uio.ifi.in2000.team6.rakett_app.model.safetyreport.DailySafetyReport
import no.uio.ifi.in2000.team6.rakett_app.model.safetyreport.SafetyReport


class SafetyReportRepository {
    private val forecast = LocationForecastRepository()
    private val gribRepository = GribRepository()

    suspend fun getSafetyReport(lat: Double, longitude: Double): SafetyReport {
        val forecastData = forecast.getForecastTimeInstant(lat, longitude)
        val gribData = gribRepository.getGribMapped(lat,longitude)


        val windSpeed = forecastData.wind_speed
        val windDirection = forecastData.wind_from_direction

        //EKSEMPEL DATA UNDER
        return SafetyReport(
            air_pressure_at_sea_level = 0.0,
            air_temperature = 0.0,
            cloud_area_fraction = 0.0,
            cloud_area_fraction_high = 0.0,
            cloud_area_fraction_low = 0.0,
            cloud_area_fraction_medium = 0.0,
            dew_point_temperature = 0.0,
            fog_area_fraction = 0.0,
            relative_humidity = 0.0,
            wind_from_direction = 0.0,
            wind_speed = 0.0,
            wind_speed_of_gust = 0.0,

            wind_sheare = listOf(0.0),
            wind_in_air = GribMap(0.0,0.0,0.0,),

            score = 0.0
        )
    }

    suspend fun getDailySafetyReport(lat: Double, longitude: Double): DailySafetyReport {
        val forecastData = forecast.getForecastTimeInstant(lat, longitude)
        val gribData = gribRepository.getGribMapped(lat,longitude)


        val windSpeed = forecastData.wind_speed
        val windDirection = forecastData.wind_from_direction

        //EKSEMPEL DATA UNDER
        return DailySafetyReport(
            safetyReports = listOf(getSafetyReport(lat,longitude)) ,
            score = 0.0
        )
    }


}
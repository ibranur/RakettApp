package no.uio.ifi.in2000.team6.rakett_app.model.safetyreport

import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap

data class SafetyReport(
    //Data fra Forecast
    val air_pressure_at_sea_level: Double,
    val air_temperature: Double,
    val cloud_area_fraction: Double,
    val cloud_area_fraction_high: Double,
    val cloud_area_fraction_low: Double,
    val cloud_area_fraction_medium: Double,
    val dew_point_temperature: Double,
    val fog_area_fraction: Double,
    val relative_humidity: Double,
    val wind_from_direction: Double,
    val wind_speed: Double,
    val wind_speed_of_gust: Double,
//Data fra Grib
    val wind_sheare: List<Double>,
    val wind_in_air: GribMap,

    val score: Double

)

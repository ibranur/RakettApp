package no.uio.ifi.in2000.team6.rakett_app.data.converter

import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

fun toMeters(hPa: Int, temp: Double): Double {
    val seaLevelPressure = 1013.25 // hPa
    val L = 0.0065 // Temperature lapse rate (K/m)
    val R = 8.314 // Universal gas constant (J/(mol·K))
    val g = 9.80665 // Gravity acceleration (m/s²)
    val M = 0.0289644 // Molar mass of dry air (kg/mol)

    val temperatureKelvin = temp + 273.15

    return (temperatureKelvin / L) * (1 - (hPa / seaLevelPressure).pow((R * L) / (g * M)))
}

fun windShear(gribList: List<GribMap>): List<Double> {
    var output = emptyList<Double>()

    for (i in gribList.indices) {
        if (i == gribList.size - 1) break
        output = output.plus(calculateWindShear(gribList[i], gribList[i+1]))
    }
    return output
}

private fun calculateWindShear(grib1: GribMap, grib2: GribMap): Double {
    val delta = Math.toRadians(grib2.wind_direction - grib1.wind_direction)
    return sqrt(
        grib1.wind_speed.pow(2) + grib2.wind_speed.pow(2) -
                2 * grib1.wind_speed * grib2.wind_speed * cos(delta)
    )
}

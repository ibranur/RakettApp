package no.uio.ifi.in2000.team6.rakett_app.data.converter

import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Data
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Forecast
import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalUnit
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

@RequiresApi(Build.VERSION_CODES.O)
fun convertToNorwegianTime(date: String): ZonedDateTime {

    // Parse the string into a ZonedDateTime object in UTC
    val utcDateTime = ZonedDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)

    // Convert to Norwegian time zone (Europe/Oslo)
    val norwegianTime = utcDateTime.withZoneSameInstant(ZoneId.of("Europe/Oslo"))

    return norwegianTime
}

data class Test(
    val time: Int,
    val air_temperature: Double,
    val wind_speed: Double,
    val wind_direction: Double,

)

@RequiresApi(Build.VERSION_CODES.O)
fun nextFiveDaysSummary(forecast: Forecast): Map<Int,List<Test>> {
    val today = ZonedDateTime.now(ZoneId.of("Europe/Oslo"))

    var temp = today
    val output: Map<Int,List<Test>> = forecast.properties.timeseries
        .filter(predicate = {convertToNorwegianTime(it.time).hour in 8..20})
        .groupBy(
            keySelector = {convertToNorwegianTime(it.time).dayOfYear},
            valueTransform = {
                Test(
                    time = convertToNorwegianTime(it.time).hour,
                    air_temperature = it.data.instant.details.air_temperature,
                    wind_speed = it.data.instant.details.wind_speed,
                    wind_direction = it.data.instant.details.wind_from_direction
                )
        }
    )

    return output
}

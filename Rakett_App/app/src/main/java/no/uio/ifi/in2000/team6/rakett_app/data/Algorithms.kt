package no.uio.ifi.in2000.team6.rakett_app.data

import android.content.Context
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.R
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast.DetailsInstant
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast.DetailsNext1Hour
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast.Forecast
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHour
import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
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
    // If there aren't at least 2 elements, we can't calculate shear
    if (gribList.size < 2) return emptyList()

    val output = mutableListOf<Double>()

    // Calculate shear between each adjacent pair
    for (i in 0 until gribList.size - 1) {
        try {
            val shear = calculateWindShear(gribList[i], gribList[i+1])
            output.add(shear)
        } catch (e: Exception) {
            // If calculation fails, add 0.0 as a fallback
            output.add(0.0)
        }
    }

    return output
}

private fun calculateWindShear(grib1: GribMap, grib2: GribMap): Double {
    // Handle invalid wind speeds
    if (grib1.wind_speed < 0 || grib2.wind_speed < 0) return 0.0

    // Calculate the change in wind direction (in radians)
    val delta = Math.toRadians(grib2.wind_direction - grib1.wind_direction)

    // Calculate wind shear using vector math
    return sqrt(
        grib1.wind_speed.pow(2) + grib2.wind_speed.pow(2) -
                2 * grib1.wind_speed * grib2.wind_speed * cos(delta)
    )
}

fun toCET(date: String): ZonedDateTime {

    // Parse the string into a ZonedDateTime object in UTC
    val utcDateTime = ZonedDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)

    // Convert to Norwegian time zone (Europe/Oslo)
    val norwegianTime = utcDateTime.withZoneSameInstant(ZoneId.of("Europe/Oslo"))

    return norwegianTime
}

fun nextFourHours(forecast: Forecast): List<FourHour> {
    val currentDay = toCET(forecast.properties.timeseries[0].time)
    val listOfHours = listOf<ZonedDateTime>(currentDay,currentDay.plusHours(1),currentDay.plusHours(2),currentDay.plusHours(3))


    val output: List<FourHour> = forecast.properties.timeseries
        .filter(predicate = {
            toCET(it.time) in listOfHours})
        .map {
            FourHour(
                detailsInstant=
                    DetailsInstant(
                        air_pressure_at_sea_level = it.data.instant.details.air_pressure_at_sea_level,
                        air_temperature = it.data.instant.details.air_temperature,
                        cloud_area_fraction = it.data.instant.details.cloud_area_fraction,
                        cloud_area_fraction_high = it.data.instant.details.cloud_area_fraction_high,
                        cloud_area_fraction_low = it.data.instant.details.cloud_area_fraction_low,
                        cloud_area_fraction_medium = it.data.instant.details.cloud_area_fraction_medium,
                        dew_point_temperature = it.data.instant.details.dew_point_temperature,
                        fog_area_fraction = it.data.instant.details.fog_area_fraction,
                        relative_humidity = it.data.instant.details.relative_humidity,
                        wind_from_direction = it.data.instant.details.wind_from_direction,
                        wind_speed = it.data.instant.details.wind_speed,
                        wind_speed_of_gust = it.data.instant.details.wind_speed_of_gust
                    ),
                detailsNext1Hour =
                    DetailsNext1Hour(
                        precipitation_amount = it.data.next_1_hours?.details!!.precipitation_amount,
                        precipitation_amount_max = it.data.next_1_hours.details.precipitation_amount_max,
                        precipitation_amount_min = it.data.next_1_hours.details.precipitation_amount_min,
                        probability_of_precipitation = it.data.next_1_hours.details.probability_of_precipitation,
                        probability_of_thunder = it.data.next_1_hours.details.probability_of_thunder
                    ),
                hour = "${toCET(it.time).hour}:00",
                symbol_code = it.data.next_1_hours.summary.symbol_code

            )}
    return output

}

fun ScoreHour(fourHour: FourHour): Int {
    var score = 10.0 // Start with perfect score

    // Wind conditions (most critical)
    score -= when {
        fourHour.detailsInstant.wind_speed > 10.0 -> 8.0
        fourHour.detailsInstant.wind_speed > 8.0 -> 6.0
        fourHour.detailsInstant.wind_speed > 6.0 -> 4.0
        fourHour.detailsInstant.wind_speed > 4.0 -> 2.0
        fourHour.detailsInstant.wind_speed > 2.0 -> 0.5
        else -> 0.0
    }

    // Precipitation
    score -= when {
        fourHour.detailsNext1Hour.precipitation_amount_max > 2.0 -> 4.0
        fourHour.detailsNext1Hour.precipitation_amount_max > 0.5 -> 2.0
        fourHour.detailsNext1Hour.precipitation_amount_max > 0.0 -> 1.0
        else -> 0.0
    }

    // Cloud cover
    score -= fourHour.detailsInstant.cloud_area_fraction / 25.0 // 0-4 point reduction

    // Thunder probability (critical for rockets)
    score -= fourHour.detailsNext1Hour.probability_of_thunder / 10.0 // 0-10 point reduction

    // Temperature effects
    if (fourHour.detailsInstant.air_temperature < 0 || fourHour.detailsInstant.air_temperature > 30) {
        score -= 1.0
    }

    // Wind gust penalties
    if (fourHour.detailsInstant.wind_speed_of_gust > fourHour.detailsInstant.wind_speed* 1.5) {
        score -= 2.0
    }

    return score.coerceIn(0.0, 10.0).toInt()
}

fun ScoreCalculator(fourHour:FourHour) {

}
//Vindhastighet finnes bare i Instant-modellen. Funksjonen tar gjennomsnittet av alle vindverdiene pr dag

fun WindSpeedAvg(forecast: Forecast): Map<Int, Double>{
    return forecast.properties.timeseries
        .groupBy(
            keySelector = { toCET(it.time).dayOfYear},
            valueTransform = {it.data.instant.details.wind_speed}
        )
        .mapValues { (_, values) ->
            (values.average()*10).roundToInt()/10.0 //runder av til ett desimal
        }
}


fun getDrawableIdByName(context: Context, resourceName: String?): Int {
    if (resourceName == null) return R.drawable.notfound
    val drawable = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    return if (drawable != 0) drawable else R.drawable.notfound
}

fun getSelectedPoint(lst: List<LaunchPoint>): String {
    return try {
        lst.first { it.selected }.name
    } catch (e: NoSuchElementException) {
        ""
    }
}
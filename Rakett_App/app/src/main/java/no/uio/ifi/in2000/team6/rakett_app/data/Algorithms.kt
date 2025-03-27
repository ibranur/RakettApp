package no.uio.ifi.in2000.team6.rakett_app.data

import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Forecast
import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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
fun toCET(date: String): ZonedDateTime {

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
    val precipitation_amount_one_hour: Double?,
    val symbol_code: String?

)


data class Test2(
    val time: ZonedDateTime,
    val air_temperature_max: Double,
    val air_temperature_min: Double,
    val precipitation_amount: Double,
    val precipitation_amount_max: Double,
    val precipitation_amount_min: Double,
    val probability_of_precipitation: Int,
    val probability_of_thunder: Double,
    val ultraviolet_index_clear_sky_max: Int,
    val symbol_code: String,
)
@RequiresApi(Build.VERSION_CODES.O)
fun hourlyForecastForGivenDay(forecast: Forecast, dayOfWeek: DayOfWeek): Map<Int,List<Test>> {
    val timeLimit = toCET(forecast.properties.timeseries[0].time).dayOfYear + 4
    val output: Map<Int,List<Test>> = forecast.properties.timeseries
        .filter(predicate = {
            toCET(it.time).dayOfWeek == dayOfWeek && toCET(it.time).dayOfYear < timeLimit})
        .groupBy(
            keySelector = { toCET(it.time).hour},
            valueTransform = {
                Test(
                    time = toCET(it.time).hour,
                    air_temperature = it.data.instant.details.air_temperature,
                    wind_speed = it.data.instant.details.wind_speed,
                    wind_direction = it.data.instant.details.wind_from_direction,
                    precipitation_amount_one_hour = it.data.next_1_hours?.details?.precipitation_amount,
                    symbol_code = it.data.next_1_hours?.summary?.symbol_code
                )
        }
    )

    return output
}

@RequiresApi(Build.VERSION_CODES.O)
fun summaryOfFiveDays(forecast:Forecast): Map<Int,List<Test2>> {
    val output: Map<Int,List<Test2>> = forecast.properties.timeseries
        .filter(predicate = { toCET(it.time).hour in 6..9  && it.data.next_12_hours != null})
        .groupBy(
            keySelector = { toCET(it.time).dayOfYear},
            valueTransform = {
                Test2(
                    time = toCET(it.time),
                    air_temperature_max = it.data.next_6_hours!!.details.air_temperature_max,
                    air_temperature_min = it.data.next_6_hours.details.air_temperature_min ,
                    precipitation_amount = it.data.next_6_hours.details.precipitation_amount ,
                    precipitation_amount_max =  it.data.next_6_hours.details.precipitation_amount_max,
                    precipitation_amount_min =  it.data.next_6_hours.details.precipitation_amount_min,
                    probability_of_precipitation =  it.data.next_12_hours!!.details.probability_of_precipitation,
                    probability_of_thunder =  it.data.next_6_hours.details.probability_of_thunder,
                    ultraviolet_index_clear_sky_max =  it.data.next_6_hours.details.ultraviolet_index_clear_sky_max,
                    symbol_code = it.data.next_12_hours.summary.symbol_code,
                )
            }
        )
    return output
}

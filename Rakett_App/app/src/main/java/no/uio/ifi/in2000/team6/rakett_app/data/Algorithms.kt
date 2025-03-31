package no.uio.ifi.in2000.team6.rakett_app.data

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team6.rakett_app.R
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Forecast
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FiveDay
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.HourlyDay
import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
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


@RequiresApi(Build.VERSION_CODES.O)
fun fiveDaysFunction(forecast: Forecast): Map<Int, FiveDay?> {
    val windAvg = WindSpeedAvg(forecast)

    val output: Map<Int, FiveDay?> = forecast.properties.timeseries
        .filter(predicate = { it.time.contains("06:00:00Z") })
        .groupBy(
            keySelector = { toCET(it.time).dayOfYear },
            valueTransform = {

                FiveDay(
                    time = toCET(it.time), //formattedtime så man får det på formen; Friday, 28. March.
                    formattedTime = toCET(it.time).format(
                        DateTimeFormatter.ofPattern(
                            "EEEE, d. MMMM",
                            Locale.ENGLISH
                        )
                    ),
                    air_temperature_max = it.data.next_6_hours!!.details.air_temperature_max.toInt(),
                    air_temperature_min = it.data.next_6_hours.details.air_temperature_min,
                    precipitation_amount = it.data.next_6_hours.details.precipitation_amount,
                    precipitation_amount_max = it.data.next_6_hours.details.precipitation_amount_max,
                    precipitation_amount_min = it.data.next_6_hours.details.precipitation_amount_min,
                    probability_of_precipitation = it.data.next_6_hours.details.probability_of_precipitation,
                    symbol_code = it.data.next_12_hours?.summary?.symbol_code,
                    wind_avg = windAvg[toCET(it.time).dayOfYear]
                )
            })
        .mapValues { (_, list) -> list.firstOrNull() }


return output
}

//Vindhastighet finnes bare i Instant-modellen. Funksjonen tar gjennomsnittet av alle vindverdiene pr dag
@RequiresApi(Build.VERSION_CODES.O)
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


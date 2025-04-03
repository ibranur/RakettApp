package no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast;

import java.time.ZonedDateTime;

data class FiveDay(
        val time:ZonedDateTime,
        val formattedTime: String,
        val air_temperature_max: Int,
        val air_temperature_min: Double,
        val precipitation_amount: Double,
        val precipitation_amount_max: Double,
        val precipitation_amount_min: Double,
        val probability_of_precipitation: Double,
        val symbol_code: String,
        val wind_avg: Double? = null
    )
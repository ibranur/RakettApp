package no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast

data class HourlyDay(
    val time: Int,
    val air_temperature: Double,
    val wind_speed: Double,
    val wind_direction: Double,
    val precipitation_amount_one_hour: Double?,
    val symbol_code: String?

)
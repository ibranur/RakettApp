package no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast

import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.DetailsInstant
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.DetailsNext1Hour

data class HourlyDay(
    val time: Int,
    val air_temperature: Double,
    val wind_speed: Double,
    val wind_direction: Double,
    val precipitation_amount_one_hour: Double?,
    val symbol_code: String?,
    val details: DetailsInstant,
    val precipDetails: DetailsNext1Hour?,
    val symbolCode: String?

    )
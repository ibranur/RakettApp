package no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast

import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.DetailsInstant
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.DetailsNext1Hour

data class FourHour(
    val detailsInstant: DetailsInstant,
    val detailsNext1Hour: DetailsNext1Hour,
    val hour: String,
    val symbol_code: String

)
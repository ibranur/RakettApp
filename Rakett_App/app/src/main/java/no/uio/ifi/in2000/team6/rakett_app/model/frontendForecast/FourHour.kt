package no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast

import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast.DetailsInstant
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast.DetailsNext1Hour

data class FourHour(
    val detailsInstant: DetailsInstant,
    val detailsNext1Hour: DetailsNext1Hour,
    val hour: String,
    val symbol_code: String

)
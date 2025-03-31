package no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact
import kotlinx.serialization.Serializable

@Serializable
data class Instant(
    val details: DetailsInstant
)
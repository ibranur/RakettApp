package no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact
import kotlinx.serialization.Serializable

@Serializable
data class DetailsNext12Hours (
    val probability_of_precipitation: Double,
)
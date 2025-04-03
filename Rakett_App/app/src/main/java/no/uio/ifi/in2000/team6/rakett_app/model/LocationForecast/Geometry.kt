package no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast
import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    val coordinates: List<Double>,
    val type: String
)
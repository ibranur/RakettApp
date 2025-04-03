package no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val instant: Instant,
    val next_12_hours: NextXHours<DetailsNext12Hours>?,
    val next_1_hours: NextXHours<DetailsNext1Hour>?,
    val next_6_hours: NextXHours<DetailsNext6Hours>?
)
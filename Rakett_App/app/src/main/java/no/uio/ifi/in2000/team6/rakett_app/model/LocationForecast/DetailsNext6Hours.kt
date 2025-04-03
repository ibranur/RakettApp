package no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast
import kotlinx.serialization.Serializable

@Serializable
data class DetailsNext6Hours (
    val air_temperature_max: Double,
    val air_temperature_min: Double,
    val precipitation_amount: Double,
    val precipitation_amount_max: Double,
    val precipitation_amount_min: Double,
    val probability_of_precipitation: Double,
)
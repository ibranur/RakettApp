package no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast

import kotlinx.serialization.Serializable


@Serializable
data class NextXHours<T>(
    val details: T,
    val summary: Summary,
)


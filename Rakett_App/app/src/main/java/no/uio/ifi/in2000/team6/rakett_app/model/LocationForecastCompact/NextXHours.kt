package no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


data class NextXHours(
    val details: DetailsNextXHours,
    val summary: Summary,
)


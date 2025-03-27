package no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact

data class Data(
    val instant: Instant,
    val next_12_hours: NextXHours?,
    val next_1_hours: NextXHours?,
    val next_6_hours: NextXHours?
)
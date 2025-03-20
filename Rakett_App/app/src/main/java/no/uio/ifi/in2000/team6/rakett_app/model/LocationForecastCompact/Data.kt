package no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact

data class Data(
    val instant: Instant,
    val next_12_hours: Next12Hours,
    val next_1_hours: Next12Hours,
    val next_6_hours: Next12Hours
)
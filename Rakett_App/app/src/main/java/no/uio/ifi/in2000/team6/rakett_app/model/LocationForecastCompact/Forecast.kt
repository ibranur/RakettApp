package no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact

data class Forecast(
    val geometry: Geometry,
    val properties: Properties,
    val type: String
)
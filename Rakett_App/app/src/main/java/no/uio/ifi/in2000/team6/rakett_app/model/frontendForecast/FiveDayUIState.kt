package no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast

data class FiveDayUIState (
    val forecast: Map<Int,FiveDay?> = emptyMap()
)
package no.uio.ifi.in2000.team6.rakett_app.ui.cards

import no.uio.ifi.in2000.team6.rakett_app.R

//Usikker på hvor disse best kan plasseres, eller om de trengs. De brukes i DateCard og HourCard, men disse kan kanskje erstattes med faktiske data. feks json returverdien.

data class GoodTimeWindow(
    val time: String,
    val details: String
)

data class DateWeatherInfo(
    val date: String,
    val weatherScore: Double,
    val goodTimeWindows: List<GoodTimeWindow>
)

enum class ReasonType(val descriptionRes: Int) {
    WIND(R.string.reason_wind), // e.g., "Wind (m/s)"
    PRECIPITATION(R.string.reason_precipitation), // e.g., "Precipitation (mm)"
    CLOUD_COVER(R.string.reason_cloud_cover), // e.g., "Cloud Cover (%)"
    HUMIDITY(R.string.reason_humidity), // e.g., "Humidity (%)"
    THUNDER(R.string.reason_thunder), // "Probability of Thunder (%)"
    CLEAR_SKY(R.string.reason_clear_sky),
}

data class Reason(
    val type: ReasonType,
    val value: String
)

data class HourWeatherInfo( // More specific name
    val hour: String, // e.g., "14:00"
    val weatherScore: Double,
    val details: List<Reason> // Use list of Reason directly
)
package no.uio.ifi.in2000.team6.rakett_app.model.grib

data class RangesMeta(
    val axisNames: List<String>,
    val dataType: String,
    val shape: List<Int>,
    val type: String,
    val values: List<Double>
)

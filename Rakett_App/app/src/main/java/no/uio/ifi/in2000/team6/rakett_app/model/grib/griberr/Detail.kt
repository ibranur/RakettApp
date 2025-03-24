package no.uio.ifi.in2000.team6.rakett_app.model.grib.griberr

data class Detail(
    val input: Double,
    val loc: List<Any>,
    val msg: String,
    val type: String
)
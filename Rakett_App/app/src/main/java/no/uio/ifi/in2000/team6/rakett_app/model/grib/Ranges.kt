package no.uio.ifi.in2000.team6.rakett_app.model.grib

data class Ranges(
    val temperature: RangesMeta,
    val wind_from_direction: RangesMeta,
    val wind_speed: RangesMeta
)
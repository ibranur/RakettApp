package no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact
import kotlinx.serialization.Serializable

@Serializable
data class Units(
    val air_pressure_at_sea_level: String,
    val air_temperature: String,
    val air_temperature_max: String,
    val air_temperature_min: String,
    val cloud_area_fraction: String,
    val cloud_area_fraction_high: String,
    val cloud_area_fraction_low: String,
    val cloud_area_fraction_medium: String,
    val dew_point_temperature: String,
    val fog_area_fraction: String,
    val precipitation_amount: String,
    val precipitation_amount_max: String,
    val precipitation_amount_min: String,
    val probability_of_precipitation: String,
    val probability_of_thunder: String,
    val relative_humidity: String,
    val ultraviolet_index_clear_sky_max: String,
    val wind_from_direction: String,
    val wind_speed: String,
    val wind_speed_of_gust: String
)
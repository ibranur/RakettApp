package no.uio.ifi.in2000.team6.rakett_app.utils

/**
 * Hjelpeklasse for formatering og validering av koordinater.
 * Inneholder metoder for å vise koordinater i lesbart format med retningsangivelser.
 */
object CoordinateUtils {
    // Formaterer breddegrad med N/S-indikator
    fun formatLatitude(latitude: Double, precision: Int = 6): String {
        val direction = if (latitude >= 0) "N" else "S"
        val absLatitude = kotlin.math.abs(latitude)
        return "${String.format("%.${precision}f", absLatitude)}° $direction"
    }

    // Formaterer lengdegrad med E/W-indikator
    fun formatLongitude(longitude: Double, precision: Int = 6): String {
        val direction = if (longitude >= 0) "E" else "W"
        val absLongitude = kotlin.math.abs(longitude)
        return "${String.format("%.${precision}f", absLongitude)}° $direction"
    }

    //TODO implement into the coordinate input fields
    // Validerer breddegrad (mellom -90 og 90)
    fun validateLatitude(latitude: String): Boolean {
        val latValue = latitude.toDoubleOrNull() ?: return false
        return latValue >= -90.0 && latValue <= 90.0
    }

    // Validerer lengdegrad (mellom -180 og 180)
    fun validateLongitude(longitude: String): Boolean {
        val lonValue = longitude.toDoubleOrNull() ?: return false
        return lonValue >= -180.0 && lonValue <= 180.0
    }

    //TODO implemnt into the weather repos
    // Formaterer vindretning basert på grader
    fun formatWindDirection(degrees: Double): String {
        val directions = arrayOf(
            "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"
        )
        val index = ((degrees % 360) / 22.5).toInt()
        return directions[index]
    }
}


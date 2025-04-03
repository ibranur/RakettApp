package no.uio.ifi.in2000.team6.rakett_app.utils

object CoordinateUtils {
    // Format latitude with directional indicator
    fun formatLatitude(latitude: Double, precision: Int = 6): String {
        val direction = if (latitude >= 0) "N" else "S"
        val absLatitude = kotlin.math.abs(latitude)
        return "${String.format("%.${precision}f", absLatitude)}° $direction"
    }

    // Format longitude with directional indicator
    fun formatLongitude(longitude: Double, precision: Int = 6): String {
        val direction = if (longitude >= 0) "E" else "W"
        val absLongitude = kotlin.math.abs(longitude)
        return "${String.format("%.${precision}f", absLongitude)}° $direction"
    }

    // Validate latitude input (between -90 and 90)
    //TODO implement into the coordinate input fields
    fun validateLatitude(latitude: String): Boolean {
        val latValue = latitude.toDoubleOrNull() ?: return false
        return latValue >= -90.0 && latValue <= 90.0
    }

    // Validate longitude input (between -180 and 180)
    fun validateLongitude(longitude: String): Boolean {
        val lonValue = longitude.toDoubleOrNull() ?: return false
        return lonValue >= -180.0 && lonValue <= 180.0
    }

    // Format wind direction
    //TODO implemnt into the weather repos
    fun formatWindDirection(degrees: Double): String {
        val directions = arrayOf(
            "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"
        )
        val index = ((degrees % 360) / 22.5).toInt()
        return directions[index]
    }
}
    package no.uio.ifi.in2000.team6.rakett_app.data.repository

    import kotlin.math.atan2
    import kotlin.math.pow
    import kotlin.math.sqrt

    class CalculationRepository {

        companion object {
            fun toMeters(hPa: Int, temp: Double): Double {
                val seaLevelPressure = 1013.25 // hPa
                val L = 0.0065 // Temperature lapse rate (K/m)
                val R = 8.314 // Universal gas constant (J/(mol·K))
                val g = 9.80665 // Gravity acceleration (m/s²)
                val M = 0.0289644 // Molar mass of dry air (kg/mol)

                val temperatureKelvin = temp + 273.15

                return (temperatureKelvin / L) * (1 - (hPa / seaLevelPressure).pow((R * L) / (g * M)))
            }
        }
    }
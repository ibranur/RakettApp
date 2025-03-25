    package no.uio.ifi.in2000.team6.rakett_app.data.repository

    import kotlin.math.atan2
    import kotlin.math.sqrt

    class CalculationRepository {

        // Regner ut hvor sterk vinden er basert på øst-vest og nord-sør retning
        fun calculateWindSpeed(u: Double, v: Double): Double {
            // u er vindstyrke i øst-vest retning, v er vindstyrke i nord-sør retning
            // Bruker pytagoras for å regne ut total vindstyrke
            return sqrt(u * u + v * v)
        }

        // Regner ut hvilken retning vinden kommer fra i grader (0-360)
        fun calculateWindDirection(u: Double, v: Double): Double {
            // Regner ut retningen vinden blåser mot
            val directionBlowingTo = atan2(u, v)

            // Gjør om til grader
            var directionDegrees = Math.toDegrees(directionBlowingTo)

            // Sørger for at vi får verdi mellom 0-360 grader
            if (directionDegrees < 0) {
                directionDegrees += 360.0
            }

            // Snur retningen for å få hvor vinden kommer fra
            directionDegrees = (directionDegrees + 180.0) % 360.0

            return directionDegrees
        }
    }
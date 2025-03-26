package no.uio.ifi.in2000.team6.rakett_app.data.repository

import no.uio.ifi.in2000.team6.rakett_app.data.GribDataSource
import no.uio.ifi.in2000.team6.rakett_app.model.grib.Grib

    class GribRepository {
    private val gribDataSource = GribDataSource()

    suspend fun getGribData(lat: Double, longitude: Double): Grib? {
        println("Fetching GRIB data for coordinates: lat=$lat, lon=$longitude")
        val gribData = gribDataSource.fetchGribFile(lat, longitude)

        println("GRIB Data received:")
        gribData?.let {
            println("ID: ${it.id}")
            println("Type: ${it.type}")
            println("Ranges:")
            println("  Temperature:")
            println("    Axis Names: ${it.ranges.temperature.axisNames}")
            println("    Data Type: ${it.ranges.temperature.dataType}")
            println("    Shape: ${it.ranges.temperature.shape}")
            println("    Values: ${it.ranges.temperature.values}")

            println("  Wind Speed:")
            println("    Axis Names: ${it.ranges.wind_speed.axisNames}")
            println("    Data Type: ${it.ranges.wind_speed.dataType}")
            println("    Shape: ${it.ranges.wind_speed.shape}")
            println("    Values: ${it.ranges.wind_speed.values}")

            println("  Wind Direction:")
            println("    Axis Names: ${it.ranges.wind_from_direction.axisNames}")
            println("    Data Type: ${it.ranges.wind_from_direction.dataType}")
            println("    Shape: ${it.ranges.wind_from_direction.shape}")
            println("    Values: ${it.ranges.wind_from_direction.values}")
        } ?: println("No GRIB data received")

        return gribData
    }
}


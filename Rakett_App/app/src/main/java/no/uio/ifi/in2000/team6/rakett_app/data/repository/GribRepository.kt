package no.uio.ifi.in2000.team6.rakett_app.data.repository

import android.util.Log
import no.uio.ifi.in2000.team6.rakett_app.data.GribDataSource
import no.uio.ifi.in2000.team6.rakett_app.data.toMeters
import no.uio.ifi.in2000.team6.rakett_app.model.grib.Grib
import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap
import no.uio.ifi.in2000.team6.rakett_app.model.grib.isobaricLayers

class GribRepository {
    private val TAG = "GribRepository"
    private val _gribDataSource = GribDataSource()

    suspend fun getGribMapped(lat: Double, longitude: Double): List<GribMap>? {
        val grib: Grib = _gribDataSource.fetchGribFile(lat, longitude) ?: return null

        try {
            // Safely check for ranges data
            if (grib.ranges == null) {
                Log.e(TAG, "GRIB data has null ranges")
                return null
            }

            // Safely get the values lists with null checks
            val tempValues = grib.ranges.temperature?.values
            val windSpeedValues = grib.ranges.wind_speed?.values
            val windDirectionValues = grib.ranges.wind_from_direction?.values

            if (tempValues == null || windSpeedValues == null || windDirectionValues == null) {
                Log.e(TAG, "GRIB data has null value lists")
                return null
            }

            if (tempValues.isEmpty() || windSpeedValues.isEmpty() || windDirectionValues.isEmpty()) {
                Log.e(TAG, "GRIB data has empty value lists")
                return null
            }

            // Calculate safe size - minimum of all list sizes
            val dataSize = minOf(
                tempValues.size,
                windSpeedValues.size,
                windDirectionValues.size,
                isobaricLayers.size
            )

            // Build the output data safely
            val outList = mutableListOf<GribMap>()

            for (i in 0 until dataSize) {
                try {
                    // Calculate altitude and check it's valid
                    val altitude = toMeters(isobaricLayers[i], tempValues[i])

                    // Skip invalid data
                    if (!altitude.isFinite() || altitude <= 0) {
                        continue
                    }

                    // Get wind values safely
                    val windDirection = windDirectionValues[i]
                    val windSpeed = windSpeedValues[i]

                    // Skip invalid wind values
                    if (!windDirection.isFinite() || !windSpeed.isFinite() || windSpeed < 0) {
                        continue
                    }

                    // Add valid data to the output list
                    outList.add(
                        GribMap(
                            altitude = altitude,
                            wind_direction = windDirection,
                            wind_speed = windSpeed
                        )
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing GRIB data at index $i: ${e.message}")
                    // Continue with next item instead of failing completely
                }
            }

            return outList.ifEmpty { null }

        } catch (e: Exception) {
            Log.e(TAG, "Error processing GRIB data: ${e.message}")
            return null
        }
    }
}
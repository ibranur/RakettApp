package no.uio.ifi.in2000.team6.rakett_app.data.repository

import no.uio.ifi.in2000.team6.rakett_app.data.GribDataSource
import no.uio.ifi.in2000.team6.rakett_app.data.toMeters
import no.uio.ifi.in2000.team6.rakett_app.model.grib.Grib
import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap
import no.uio.ifi.in2000.team6.rakett_app.model.grib.isobaricLayers

class GribRepository {
    private val _gribDataSource = GribDataSource()

    suspend fun getGribMapped(lat: Double, longitude: Double): List<GribMap>? {
        val grib: Grib = _gribDataSource.fetchGribFile(lat, longitude) ?: return null

        var outList: List<GribMap> = emptyList<GribMap>()
        val temp: List<Triple<Double,Double,Double>>
        val tempList: List<Double> = grib.ranges.temperature.values
        val windSpeedList: List<Double> = grib.ranges.wind_speed.values
        val windDirectionList: List<Double> = grib.ranges.wind_from_direction.values



        if  (tempList.size == windDirectionList.size && windDirectionList.size == windSpeedList.size) {
            for (i in tempList.indices) {

                outList = outList.plus(
                    GribMap(
                        toMeters(isobaricLayers[i],tempList[i]),
                        windDirectionList[i],
                        windSpeedList[i]
                        )
                )

            }
        }

        return outList

}

}
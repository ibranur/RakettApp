package no.uio.ifi.in2000.team6.rakett_app.data.repository

import no.uio.ifi.in2000.team6.rakett_app.data.GribDataSource
import no.uio.ifi.in2000.team6.rakett_app.model.grib.Grib

class GribRepository {
    private val gribDataSource = GribDataSource()

    suspend fun getGribData(lat: Double, longitude: Double): Grib? {
        return gribDataSource.fetchGribFile(lat, longitude)
    }
}


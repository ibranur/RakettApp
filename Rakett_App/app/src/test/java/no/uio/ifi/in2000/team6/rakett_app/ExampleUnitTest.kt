package no.uio.ifi.in2000.team6.rakett_app

import android.util.Log
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team6.rakett_app.data.GribDataSource
import no.uio.ifi.in2000.team6.rakett_app.data.LocationForecastDatasource
import no.uio.ifi.in2000.team6.rakett_app.data.repository.GribRepository
import no.uio.ifi.in2000.team6.rakett_app.data.repository.LocationForecastRepository
import no.uio.ifi.in2000.team6.rakett_app.model.grib.Grib
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }



    @Test
    fun locforecast() {

         val lat = 59.91386880
         val long = 10.75224540
        val locationForecastDS = LocationForecastDatasource()
        val rep = LocationForecastRepository()

        var output = ""
        runBlocking{ output = rep.getForecastTimeInstant(lat,long).toString() }

//        Log.e("TAG",output)
       println(output)
    }

    @Test
    fun gribDSTest() {
        val ds = GribDataSource()
        val lat = 59.9138
        val long = 10.7522
        var output: Grib?
        runBlocking{output = ds.fetchGribFile(lat, long)}

        println(output.toString())
        println(output?.ranges!!.temperature.values)
        println(output?.ranges!!.wind_from_direction.values)
        println(output?.ranges!!.wind_speed.values)
    }

    @Test
    fun gribRepo() {

        val rep = GribRepository()
        val lat = 59.9138
        val long = 10.7522

        runBlocking { println(rep.getGribMapped(lat,long).toString())}

    }
}
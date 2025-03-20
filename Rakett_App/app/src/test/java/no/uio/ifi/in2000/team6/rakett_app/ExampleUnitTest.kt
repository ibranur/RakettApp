package no.uio.ifi.in2000.team6.rakett_app

import android.util.Log
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team6.rakett_app.data.LocationForecastDatasource
import no.uio.ifi.in2000.team6.rakett_app.data.LocationForecastRepository
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


}
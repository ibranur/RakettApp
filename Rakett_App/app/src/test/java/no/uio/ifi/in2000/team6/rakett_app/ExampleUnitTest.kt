package no.uio.ifi.in2000.team6.rakett_app

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team6.rakett_app.data.GribDataSource
import no.uio.ifi.in2000.team6.rakett_app.data.LocationForecastDatasource
import no.uio.ifi.in2000.team6.rakett_app.data.fiveDaysFunction
import no.uio.ifi.in2000.team6.rakett_app.data.nextFourHours
import no.uio.ifi.in2000.team6.rakett_app.data.windShear
import no.uio.ifi.in2000.team6.rakett_app.data.repository.GribRepository
import no.uio.ifi.in2000.team6.rakett_app.data.repository.LocationForecastRepository
import no.uio.ifi.in2000.team6.rakett_app.data.toCET
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Forecast
import no.uio.ifi.in2000.team6.rakett_app.model.grib.Grib
import org.junit.Test

import org.junit.Assert.*
import java.time.DayOfWeek

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

        val lat = 59.9138
        val long = 10.7522
        val locationForecastDS = LocationForecastDatasource()
        var forecast: Forecast?
        runBlocking{forecast = locationForecastDS.fetchForecast(lat, long)}

        if (forecast == null) return
        println(forecast!!.properties.timeseries
            .forEach {println(it.data.next_6_hours?.details)}
        )

//        runBlocking {
//            hourlyForecastForGivenDay(locationForecastDS.fetchForecast(lat, long)!!, DayOfWeek.FRIDAY)
//            .forEach {
//                (key, value) ->
//                println("$key : $value")
//            }
//        }
    }

    @Test
    fun testConv() {
        println(toCET("2025-04-04T06:00:00Z"))
    }
    @Test
    fun summaryFive() {

        val lat = 59.9138
        val long = 10.7522
        val locationForecastDS = LocationForecastDatasource()
        val rep = LocationForecastRepository()


        runBlocking {
            fiveDaysFunction(locationForecastDS.fetchForecast(lat, long)!!).forEach {
                println(it)
            }
        }
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

    @Test
    fun windShearTest() {


        val rep = GribRepository()

        val lat = 61.42//59.9138
        val long = 4.36//10.7522

        runBlocking {

            val grblist =  rep.getGribMapped(lat,long)
            if (grblist != null) windShear(grblist).forEach { println(it) }
            else print("fail")

        }
    }


    @Test
    fun NextFourHour() {

        val lat = 59.9138
        val long = 10.7522
        val locationForecastDS = LocationForecastDatasource()
        val rep = LocationForecastRepository()


        runBlocking {
            nextFourHours(locationForecastDS.fetchForecast(lat, long)!!).forEach{println(it)}
        }
    }


}
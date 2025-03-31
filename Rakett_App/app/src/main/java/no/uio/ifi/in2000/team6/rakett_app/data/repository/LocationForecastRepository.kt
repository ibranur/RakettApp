package no.uio.ifi.in2000.team6.rakett_app.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team6.rakett_app.data.LocationForecastDatasource
import no.uio.ifi.in2000.team6.rakett_app.data.fiveDaysFunction
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FiveDay
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.DetailsInstant
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Forecast

class LocationForecastRepository {
    private val _locationForecastDatasource = LocationForecastDatasource()

    suspend fun getForecastTimeInstant(lat: Double, longitude: Double): DetailsInstant {
        val forecast: Forecast? = _locationForecastDatasource.fetchForecast(lat, longitude)

        val output: DetailsInstant = forecast!!.properties.timeseries[0].data.instant.details

        println("TIME: " + forecast.properties.timeseries[0].time)

        return output
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getFiveDayForecast(lat: Double, longitude: Double): Map<Int,FiveDay?> {
        val forecast: Forecast? = _locationForecastDatasource.fetchForecast(lat, longitude)

        if (forecast == null) return emptyMap()

        return fiveDaysFunction(forecast)
    }





}
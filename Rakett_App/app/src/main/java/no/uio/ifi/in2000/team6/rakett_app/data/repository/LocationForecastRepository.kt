package no.uio.ifi.in2000.team6.rakett_app.data.repository

import no.uio.ifi.in2000.team6.rakett_app.data.LocationForecastDatasource
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Details
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Forecast

class LocationForecastRepository {
    private val _locationForecastDatasource = LocationForecastDatasource()




    suspend fun getForecastTimeInstant(lat: Double, longitude: Double): Details {
        val forecast: Forecast? = _locationForecastDatasource.fetchForecast(lat, longitude)

        val output: Details = forecast!!.properties.timeseries[0].data.instant.details

        println("TIME: " + forecast.properties.timeseries[0].time)

        return output
    }




}
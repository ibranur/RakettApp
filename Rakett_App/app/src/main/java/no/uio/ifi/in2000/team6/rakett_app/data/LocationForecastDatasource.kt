package no.uio.ifi.in2000.team6.rakett_app.data

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import no.uio.ifi.in2000.team6.rakett_app.model.APIClient
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Forecast
import java.net.UnknownHostException

class LocationForecastDatasource {

    private val lat = 59.91386880
    private val long = 10.75224540
    val USER_AGENT  ="https://github.uio.no/IN2000-V25/team-6"


    suspend fun fetchForecast(lat: Double, longitude: Double): Forecast? {
        val url: String = "https://in2000.api.met.no/weatherapi/locationforecast/2.0/compact?lat=$lat&lon=$longitude"

        val forecast: Forecast? = try {
            val response: HttpResponse = APIClient.client.get(
                url
            )
            if(response.status.value != 200) {
                null
            } else {
                response.body()
            }

        }

        catch(e: UnknownHostException) {
            null
        }


        return forecast
    }
}
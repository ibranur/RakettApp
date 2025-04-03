package no.uio.ifi.in2000.team6.rakett_app.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import no.uio.ifi.in2000.team6.rakett_app.model.APIClient
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Forecast
import java.net.UnknownHostException

class LocationForecastDatasource {
    private val TAG = "LocationForecastDatasource" // Tag for Logcat

    suspend fun fetchForecast(latitude: Double, longitude: Double): Forecast? {
        val apiUrl = "https://in2000.api.met.no/weatherapi/locationforecast/2.0/complete"
        val url = "$apiUrl?lat=$latitude&lon=$longitude"

        Log.d(TAG, "Henter data fra: $url")

        val forecast: Forecast? = try {
            val response: HttpResponse = APIClient.client.get(url)

            if (response.status.value != 200) {
                Log.e(TAG, "Feil ved henting av LocationForecast. Statuskode: ${response.status.value}")
                null
            } else {
                Log.d(TAG, "LocationForecast hentet OK. Lager Forecast objekt...")
                val forecastData: Forecast = response.body()
                Log.d(TAG, "Forecast objekt laget fra LocationForecast og returnert.")
                forecastData // Returner resultatet
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Kunne ikke koble til API: ${e.message}\"")

            null
        } catch (e: Exception) {
            Log.e(TAG, "En uventet feil oppstod: ${e.message}")
            null
        }
        return forecast
    }
}
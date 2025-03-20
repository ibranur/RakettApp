package no.uio.ifi.in2000.team6.rakett_app.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecastCompact.Forecast
import org.slf4j.LoggerFactory

class LocationForecastDatasource {

    companion object {
        private const val ENDPOINT = "https://api.met.no/weatherapi/locationforecast/2.0/compact?"
    }

    private val client = HttpClient(OkHttp) {
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    Log.i("HttpClient", message)
                }
            }
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun fetchLocationForecast(): Forecast {
        return try {
            val response: HttpResponse = client.get(ENDPOINT+"lat=59.9138&lon=10.7522")

            if (!response.status.isSuccess()) {
                val responseBody = response.bodyAsText()
//                Log.e("Failed to fetch parties. HTTP status: ${response.status}, Error body: $responseBody")
                throw NetworkException("Failed to fetch parties. HTTP status: ${response.status}, Error body: $responseBody")
            }

            // Directly deserialize the response body into a PartyResponse object
            val partyResponse: PartyResponse = response.body()

//            logger.info("Successfully fetched ${partyResponse.parties.size} parties.")
            partyResponse.parties
        } catch (e: Exception) {
//            logger.error("Failed to fetch parties: ${e.message}", e)
            throw NetworkException("Failed to fetch parties: ${e.message}")
        }
}
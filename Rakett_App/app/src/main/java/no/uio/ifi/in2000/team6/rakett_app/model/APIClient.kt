package no.uio.ifi.in2000.team6.rakett_app.model

import android.util.Log // Importer Logcat for logging
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.gson.gson

object APIClient {
    private const val USER_AGENT = "https://github.uio.no/IN2000-V25/team-6"
    private const val TAG = "APIClient" // Tag for Logcat messages

    val client = HttpClient(Android) {
        install(ContentNegotiation) {
            gson()
        }
        install(DefaultRequest) {
            header(HttpHeaders.UserAgent, USER_AGENT)
        }
    }.also {
        Log.i(TAG, "Ktor HttpClient initialisert med User-Agent: $USER_AGENT")
    }

    // Husk å lukke klienten når den ikke lenger er i bruk, for å frigjøre ressurser. feks:
    // fun close() {
    //     client.close()
    //     Log.i(TAG, "Ktor HttpClient lukket.")
    // }
}
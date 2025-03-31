package no.uio.ifi.in2000.team6.rakett_app.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import no.uio.ifi.in2000.team6.rakett_app.model.APIClient
import no.uio.ifi.in2000.team6.rakett_app.model.grib.Grib
import no.uio.ifi.in2000.team6.rakett_app.model.grib.griberr.Griberr
import java.net.UnknownHostException

class GribDataSource {
    private val TAG = "GribDataSource"

    suspend fun fetchGribFile(latitude: Double, longitude: Double): Grib? {
        val url = "http://69.62.118.138:5000/collections/weather_forecast/position?coords=POINT%28$longitude%20$latitude%29"
        // NB: POINT%28$longitude%20$latitude%29 rekkefølgen er (longitude, latitude) ikke (latitude, longitude) som i locationforecast
        Log.d(TAG, "Henter GRIB-fil fra: $url")

        val grib: Grib? = try {
            val response: HttpResponse = APIClient.client.get(url)

            if (response.status.value != 200) {
                if (response.status.value == 422) {
                    val error = fetchGribError(response)
                    Log.e(TAG, "Feil ved henting av GRIB-fil (422): ${error.detail.getOrNull(0)?.msg}")
                    // Bruker getOrNull for å unngå IndexOutOfBoundsException hvis detail er tom
                } else {
                    Log.e(TAG, "Feil ved henting av GRIB-fil. Statuskode: ${response.status.value}. Beskrivelse: ${response.status.description}. ")
                }
                null
            } else {
                Log.d(TAG, "GRIB-fil hentet OK. lager Grib-objekt...")
                val gribData: Grib = response.body()
                Log.d(TAG, "Grib-objekt laget fra GRIB-fil og returnert.")
                gribData
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Kunne ikke koble til server. error: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e(TAG, "En uventet feil oppstod: ${e.message}")
            null
        }
        return grib
    }

    private suspend fun fetchGribError(response: HttpResponse): Griberr {
        // Denne funksjonen er strengt tatt ikke nødvendig, men hvis man f.eks. sender ugyldige parametere til API-et,
        // så sender det HTTP-feilkode 422 med et eget feilmeldingsobjekt (Griberr). Da er det fint å kunne serialisere
        // feilmeldingen for å lese i loggen hva som feilet.
        return try {
            val gribError: Griberr = response.body()
            Log.w(TAG, "Deserialiserte Griberr: ${gribError.detail.getOrNull(0)?.msg}")
            gribError
        } catch (e: Exception) {
            Log.e(TAG, "Feil ved deserialisering av Griberr: ${e.message}")
            Griberr(emptyList()) // Returnerer en tom Griberr for å unngå krasj
        }
    }
}
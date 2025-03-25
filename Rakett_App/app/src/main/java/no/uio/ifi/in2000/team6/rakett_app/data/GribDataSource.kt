package no.uio.ifi.in2000.team6.rakett_app.data

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import no.uio.ifi.in2000.team6.rakett_app.model.APIClient
import no.uio.ifi.in2000.team6.rakett_app.model.grib.Grib
import no.uio.ifi.in2000.team6.rakett_app.model.grib.griberr.Griberr
import java.net.UnknownHostException

class GribDataSource {

    suspend fun fetchGribFile(lat: Double, longitude: Double): Grib? {
        val url: String = "http://69.62.118.138:5000/collections/weather_forecast/position?coords=POINT%28$longitude%20$lat%29"
        //NB POINT%28$longitude%20$lat%29"  rekkefølgen er (longitude, latitude) ikke (latitude, longitude) som i locationforecast

        val grib: Grib? = try {
            val response: HttpResponse = APIClient.client.get(
                url
            )
            if(response.status.value != 200) {
                if(response.status.value == 422) println(fetchGribError(response).detail[0].msg)
                null
            } else {
                response.body()
            }
        }

        catch(e: UnknownHostException) {
            null
        }

        return grib
    }

    private suspend fun fetchGribError(response: HttpResponse): Griberr {
        //Denne funksjonen er strengt tatt ikke nødvendig
        //men hvis man feks sender ugyldige parameter til APIet så sender den
        //HTTP-feilkode 422 med et eget feilmeldingsobjekt (Griberr). Da er det fint å kunne serialisere feilmeldingen
        //for å lese i loggen hva som feilet.
        val griberr: Griberr = response.body()
        return griberr
    }
}
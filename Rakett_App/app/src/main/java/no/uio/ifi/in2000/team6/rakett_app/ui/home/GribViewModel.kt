package no.uio.ifi.in2000.team6.rakett_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team6.rakett_app.data.repository.GribRepository
import no.uio.ifi.in2000.team6.rakett_app.data.windShear
import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap

class GribViewModel : ViewModel() {

    private val gribRepository = GribRepository()

    private val _gribMaps = MutableStateFlow<List<GribMap>>(emptyList())
    val gribMaps: StateFlow<List<GribMap>> = _gribMaps.asStateFlow()

    private val _windShearValues = MutableStateFlow<List<Double>>(emptyList())
    val windShearValues: StateFlow<List<Double>> = _windShearValues.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun fetchGribData(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val gribMapList = gribRepository.getGribMapped(latitude, longitude)

                if (!gribMapList.isNullOrEmpty()) {
                    val sortedList = gribMapList.sortedBy { it.altitude }
                    _gribMaps.value = sortedList

                    _windShearValues.value = windShear(sortedList)
                } else {
                    //Trenger kanskje bedre løsning? se linje 130 i AltitudeWeatherCard
                    _errorMessage.value = ""
                }
            } catch (e: Exception) {
                _errorMessage.value = "Feil ved henting av høydevind-data: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

}
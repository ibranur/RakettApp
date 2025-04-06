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
import android.util.Log
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin

class GribViewModel : ViewModel() {
    private val tag = "GribViewModel"
    private val gribRepository = GribRepository()

    private val _gribMaps = MutableStateFlow<List<GribMap>>(emptyList())
    val gribMaps: StateFlow<List<GribMap>> = _gribMaps.asStateFlow()

    private val _windShearValues = MutableStateFlow<List<Double>>(emptyList())
    val windShearValues: StateFlow<List<Double>> = _windShearValues.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Track current coordinates
    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)

    // Track the location name for internal use
    private val _locationName = MutableStateFlow<String?>(null)
    val locationName = _locationName.asStateFlow()

    // Keep track of active job to be able to cancel it
    private var activeJob: Job? = null

    // Keep the data between screen transitions
    private var cachedGribMaps = emptyList<GribMap>()
    private var cachedWindShear = emptyList<Double>()

    fun fetchGribData(latitude: Double, longitude: Double, locationName: String? = null) {
        // If we're already showing data for this location, don't refetch
        val currentCoords = _currentLocation.value
        if (currentCoords != null &&
            currentCoords.first == latitude &&
            currentCoords.second == longitude &&
            cachedGribMaps.isNotEmpty()) {

            // Use cached data immediately
            _gribMaps.value = cachedGribMaps
            _windShearValues.value = cachedWindShear
            _locationName.value = locationName

            Log.d(tag, "Using cached GRIB data for $locationName (${cachedGribMaps.size} altitude levels)")
            return
        }

        // Cancel any ongoing job first
        activeJob?.cancel()

        // Start a new job
        activeJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                Log.d(tag, "Fetching GRIB data for: $locationName (lat: $latitude, lon: $longitude)")

                // Update current location
                _currentLocation.value = Pair(latitude, longitude)
                _locationName.value = locationName

                val gribMapList = gribRepository.getGribMapped(latitude, longitude)

                if (!gribMapList.isNullOrEmpty()) {
                    val sortedList = gribMapList.sortedBy { it.altitude }
                    Log.d(tag, "Received GRIB data for $locationName - ${sortedList.size} altitude levels")

                    // Update cache
                    cachedGribMaps = sortedList
                    cachedWindShear = windShear(sortedList)

                    // Update observable values
                    _gribMaps.value = sortedList
                    _windShearValues.value = cachedWindShear
                } else {
                    Log.w(tag, "No GRIB data available for $locationName")
                    _errorMessage.value = "Ingen tilgjengelig høydedata - kan kun vise data for Sør-Norge"

                    // Clear cache for this location
                    cachedGribMaps = emptyList()
                    cachedWindShear = emptyList()

                    // Update observable values
                    _gribMaps.value = emptyList()
                    _windShearValues.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e(tag, "Error fetching GRIB data for $locationName", e)
                _errorMessage.value = "Feil ved henting av høydevind-data: ${e.localizedMessage}"

                // Don't clear cache in case of temporary error

                // Update observable values if we don't have cached data
                if (cachedGribMaps.isEmpty()) {
                    _gribMaps.value = emptyList()
                    _windShearValues.value = emptyList()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearData() {
        // Only clear in-memory cache, not observable values
        // This allows data to remain visible when switching between tabs
        cachedGribMaps = emptyList()
        cachedWindShear = emptyList()
        _currentLocation.value = null
        _locationName.value = null
    }
}
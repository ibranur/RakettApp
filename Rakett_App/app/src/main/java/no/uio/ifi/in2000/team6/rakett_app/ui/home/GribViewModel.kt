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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

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

    // Track current location information
    private val _currentLocationId = MutableStateFlow<Int?>(null)
    private val _locationName = MutableStateFlow<String?>(null)
    val locationName = _locationName.asStateFlow()

    // Keep track of active job to be able to cancel it
    private var activeJob: Job? = null

    // Cache optimizations
    private val cache = mutableMapOf<String, CacheEntry>()
    private val loadingStatuses = mutableMapOf<String, Boolean>()

    private data class CacheEntry(
        val gribMaps: List<GribMap>,
        val windShear: List<Double>,
        val locationId: Int?, // Store the location ID for verification
        val timestamp: Long // Add timestamp for cache invalidation
    )

    // Static companion object to maintain state across instances
    companion object {
        private val selectedLocationId = MutableStateFlow<Int?>(null)

        // Method to get the current selected ID
        fun getSelectedLocationId(): Int? {
            return selectedLocationId.value
        }

        // Method to set the selected ID
        fun setSelectedLocationId(id: Int?) {
            selectedLocationId.value = id
        }
    }

    // Instance methods to access companion object
    fun getSelectedLocationId(): Int? = Companion.getSelectedLocationId()

    fun setSelectedLocationId(id: Int?) {
        Companion.setSelectedLocationId(id)
    }

    init {
        // Monitor the persistent location ID to keep data in sync
        viewModelScope.launch {
            selectedLocationId.collectLatest { locationId ->
                if (locationId != _currentLocationId.value) {
                    Log.d(tag, "Location ID changed to $locationId, current is ${_currentLocationId.value}")
                    _currentLocationId.value = locationId
                }
            }
        }
    }

    fun fetchGribData(latitude: Double, longitude: Double, locationName: String? = null, locationId: Int? = null) {
        // Performance optimization: Avoid redundant fetches
        val actualLocationId = locationId ?: selectedLocationId.value
        val cacheKey = "${latitude}_${longitude}"

        // If we're already loading this location, don't start a new fetch
        if (loadingStatuses[cacheKey] == true) {
            Log.d(tag, "Already loading data for $locationName, skipping duplicate fetch")
            return
        }

        // Check for fresh cached data (< 30 minutes old)
        val cachedData = cache[cacheKey]
        val isCacheFresh = cachedData != null &&
                (System.currentTimeMillis() - cachedData.timestamp) < 30 * 60 * 1000 &&
                cachedData.locationId == actualLocationId

        if (isCacheFresh) {
            Log.d(tag, "Using fresh cached GRIB data for $locationName")
            _locationName.value = locationName
            _currentLocationId.value = actualLocationId
            _gribMaps.value = cachedData!!.gribMaps
            _windShearValues.value = cachedData.windShear
            _isLoading.value = false
            _errorMessage.value = null
            return
        }

        // Mark this location as loading
        loadingStatuses[cacheKey] = true

        // Cancel any ongoing job first
        viewModelScope.launch {
            try {
                activeJob?.cancelAndJoin()
            } catch (e: Exception) {
                Log.e(tag, "Error canceling job: ${e.message}")
            }

            // Start a new job for fetching
            activeJob = viewModelScope.launch {
                try {
                    Log.d(tag, "Fetching GRIB data for $locationName (ID: $actualLocationId)")

                    // Update tracking info immediately
                    withContext(Dispatchers.Main) {
                        _isLoading.value = true
                        _locationName.value = locationName
                        _currentLocationId.value = actualLocationId

                        // Critical: CLEAR existing data when switching locations
                        // But only if we don't have fresh cached data
                        if (!isCacheFresh) {
                            _gribMaps.value = emptyList()
                            _windShearValues.value = emptyList()
                        }
                        _errorMessage.value = null
                    }

                    // Fetch data from repository
                    val gribMapList = withContext(Dispatchers.IO) {
                        gribRepository.getGribMapped(latitude, longitude)
                    }

                    // Verify we're still processing the correct location
                    if (actualLocationId != _currentLocationId.value) {
                        Log.d(tag, "Location changed during fetch, discarding results")
                        loadingStatuses[cacheKey] = false
                        return@launch
                    }

                    // Process the results
                    if (gribMapList != null && gribMapList.isNotEmpty()) {
                        val sortedList = gribMapList.sortedBy { it.altitude }
                        Log.d(tag, "Received GRIB data for $locationName - ${sortedList.size} altitude levels")

                        // Calculate wind shear
                        val shearValues = if (sortedList.size > 1) windShear(sortedList) else emptyList()

                        // Cache the results with location ID and timestamp
                        cache[cacheKey] = CacheEntry(
                            gribMaps = sortedList,
                            windShear = shearValues,
                            locationId = actualLocationId,
                            timestamp = System.currentTimeMillis()
                        )

                        // Update UI
                        withContext(Dispatchers.Main) {
                            // Only update if we're still on the same location
                            if (actualLocationId == _currentLocationId.value) {
                                _gribMaps.value = sortedList
                                _windShearValues.value = shearValues
                                _errorMessage.value = null
                            } else {
                                Log.d(tag, "Location changed before UI update, discarding results")
                            }
                            _isLoading.value = false
                        }
                    } else {
                        // No data available for this location
                        Log.d(tag, "No GRIB data available for $locationName")
                        withContext(Dispatchers.Main) {
                            // Ensure we clear any existing data
                            _gribMaps.value = emptyList()
                            _windShearValues.value = emptyList()
                            _errorMessage.value = "Ingen tilgjengelig høydedata - kan kun vise data for Sør-Norge"
                            _isLoading.value = false
                        }
                    }
                } catch (e: Exception) {
                    // Handle errors
                    Log.e(tag, "Error fetching GRIB data: ${e.message}")
                    withContext(Dispatchers.Main) {
                        _gribMaps.value = emptyList()
                        _windShearValues.value = emptyList()
                        _errorMessage.value = "Feil ved henting av høydevind-data: ${e.message}"
                        _isLoading.value = false
                    }
                } finally {
                    // Always mark loading as complete
                    loadingStatuses[cacheKey] = false
                }
            }
        }
    }

    fun clearData() {
        viewModelScope.launch {
            try {
                activeJob?.cancelAndJoin()

                withContext(Dispatchers.Main) {
                    _gribMaps.value = emptyList()
                    _windShearValues.value = emptyList()
                    _locationName.value = null
                    _errorMessage.value = null
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e(tag, "Error in clearData: ${e.message}")
            }
        }
    }

    fun clearCache() {
        cache.clear()
        loadingStatuses.clear()
    }

    // Auto-clean cache
    fun cleanExpiredCache() {
        val currentTime = System.currentTimeMillis()
        val expiredEntries = cache.filter { (_, entry) ->
            (currentTime - entry.timestamp) > 60 * 60 * 1000 // 1 hour
        }.keys

        expiredEntries.forEach { key ->
            cache.remove(key)
        }
    }
}
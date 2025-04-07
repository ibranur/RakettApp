package no.uio.ifi.in2000.team6.rakett_app.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState
import no.uio.ifi.in2000.team6.rakett_app.data.repository.LocationForecastRepository
import no.uio.ifi.in2000.team6.rakett_app.data.CoordinatesManager
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository
import no.uio.ifi.in2000.team6.rakett_app.data.repository.LaunchPointRepositoryInterface
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHour
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHourUIState

class HomeScreenViewModel(
    private val repository: SafetyReportRepository,
    private val launchPointRepository: LaunchPointRepositoryInterface? = null
) : ViewModel() {
    private val tag = "HomeScreenViewModel"

    // Forecast data
    private val _fourHourUIState = MutableStateFlow(FourHourUIState())
    val fourHourUIState = _fourHourUIState.asStateFlow()

    // Launch points state
    private val _launchPointState = MutableStateFlow(LaunchPointState())
    val launchPointState = _launchPointState.asStateFlow()

    // Currently selected location for UI
    private val _selectedLocation = MutableStateFlow<LaunchPoint?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()

    // Track current coordinates
    private val _currentCoordinates = MutableStateFlow<Pair<Double, Double>?>(null)

    // Keep track of active forecast job
    private var activeForecastJob: Job? = null

    // Repository for weather data
    private val _locationForecastRepository = LocationForecastRepository()

    // Cache for forecast data with time-based expiration
    private val forecastCache = mutableMapOf<String, CachedForecast>()

    // Data class for forecast cache
    private data class CachedForecast(
        val forecast: List<FourHour>,
        val timestamp: Long
    )

    init {
        Log.d(tag, "Initializing HomeScreenViewModel")
        // Observe the repository for any changes to launch points
        viewModelScope.launch {
            try {
                launchPointRepository?.getAllLaunchPoints()?.collectLatest { points ->
                    Log.d(tag, "Got ${points.size} launch points from repository")

                    // Update UI state with new launch points
                    _launchPointState.update {
                        it.copy(launchPoints = points)
                    }

                    // Find selected point
                    val selectedPoint = points.find { it.selected }
                    if (selectedPoint != null) {
                        Log.d(tag, "Found selected point in DB flow: ${selectedPoint.name}")

                        // Only update if the location actually changed
                        if (_selectedLocation.value?.id != selectedPoint.id) {
                            Log.d(tag, "Updating selected location to: ${selectedPoint.name}")
                            _selectedLocation.value = selectedPoint
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Error collecting launch points", e)
            }
        }
    }

    /**
     * Update UI to reflect selected location without triggering fetches
     */
    fun ensureSelectedLocationConsistency(launchPoint: LaunchPoint) {
        viewModelScope.launch(Dispatchers.Main) {
            // Only update if needed
            if (_selectedLocation.value?.id != launchPoint.id) {
                _selectedLocation.value = launchPoint
            }
        }
    }

    /**
     * Select a new location and fetch its weather data (with option to force fetch)
     */
    fun selectLocation(launchPoint: LaunchPoint, forceFetch: Boolean = true) {
        viewModelScope.launch {
            try {
                Log.d(tag, "Selecting location: ${launchPoint.name}")

                // First update UI state immediately
                _selectedLocation.value = launchPoint

                // Check if we have cached data
                val cacheKey = "${launchPoint.latitude}_${launchPoint.longitude}"
                val cachedForecast = forecastCache[cacheKey]
                val isCacheValid = cachedForecast != null &&
                        (System.currentTimeMillis() - cachedForecast.timestamp) < 30 * 60 * 1000 // 30 minutes

                if (isCacheValid && !forceFetch) {
                    // Use cached data immediately
                    Log.d(tag, "Using cached forecast data for: ${launchPoint.name}")
                    _fourHourUIState.update {
                        it.copy(list = cachedForecast!!.forecast)
                    }
                } else if (forceFetch || _currentCoordinates.value != Pair(launchPoint.latitude, launchPoint.longitude)) {
                    // Show loading state if we're forcing a fetch or location changed
                    _fourHourUIState.update {
                        it.copy(list = emptyList())
                    }
                }

                // Update the database
                withContext(Dispatchers.IO) {
                    launchPointRepository?.deselectAllLaunchPoints()
                    launchPointRepository?.updateLaunchPoint(launchPoint.copy(selected = true))
                }

                // Update local state for immediate feedback
                _launchPointState.update { state ->
                    val updatedLaunchPoints = state.launchPoints.map { point ->
                        if (point.id == launchPoint.id) {
                            point.copy(selected = true)
                        } else {
                            point.copy(selected = false)
                        }
                    }
                    state.copy(launchPoints = updatedLaunchPoints)
                }

                // Update coordinates manager only if forcing fetch or coordinates changed
                if (forceFetch || _currentCoordinates.value != Pair(launchPoint.latitude, launchPoint.longitude)) {
                    _currentCoordinates.value = Pair(launchPoint.latitude, launchPoint.longitude)
                    CoordinatesManager.updateLocation(launchPoint.latitude, launchPoint.longitude)

                    // Fetch weather data if needed
                    if (!isCacheValid || forceFetch) {
                        getFourHourForecast(launchPoint.latitude, launchPoint.longitude)
                    }
                }

            } catch (e: Exception) {
                Log.e(tag, "Error selecting location", e)
            }
        }
    }

    /**
     * Fetch 4-hour forecast data for specific coordinates
     */
    fun getFourHourForecast(latitude: Double, longitude: Double) {
        // Cancel any active job first
        activeForecastJob?.cancel()

        // Check for cached data
        val cacheKey = "${latitude}_${longitude}"
        val cachedForecast = forecastCache[cacheKey]
        val isCacheValid = cachedForecast != null &&
                (System.currentTimeMillis() - cachedForecast.timestamp) < 30 * 60 * 1000 // 30 minutes

        if (isCacheValid) {
            // Use cached data immediately
            Log.d(tag, "Using cached forecast data for: $latitude, $longitude")
            _fourHourUIState.update {
                it.copy(list = cachedForecast!!.forecast)
            }
            return
        }

        // Start new job to get fresh data
        activeForecastJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d(tag, "Getting forecast for lat: $latitude, lon: $longitude")

                // Show loading state
                withContext(Dispatchers.Main) {
                    _fourHourUIState.update {
                        it.copy(list = emptyList())
                    }
                }

                // Fetch new forecast data
                val fourHourForecast = _locationForecastRepository.getNextFourHourForecast(latitude, longitude)
                Log.d(tag, "Got forecast with ${fourHourForecast.size} hours")

                // Cache the new data with timestamp
                forecastCache[cacheKey] = CachedForecast(
                    forecast = fourHourForecast,
                    timestamp = System.currentTimeMillis()
                )

                // Update UI with new data
                withContext(Dispatchers.Main) {
                    _fourHourUIState.update {
                        it.copy(list = fourHourForecast)
                    }
                }

            } catch (e: Exception) {
                Log.e(tag, "Error getting forecast", e)
                withContext(Dispatchers.Main) {
                    _fourHourUIState.update {
                        it.copy(list = emptyList())
                    }
                }
            }
        }
    }

    /**
     * Clear weather data cache
     */
    fun clearWeatherData() {
        forecastCache.clear()
        _fourHourUIState.update {
            it.copy(list = emptyList())
        }
    }
}
package no.uio.ifi.in2000.team6.rakett_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState
import no.uio.ifi.in2000.team6.rakett_app.data.repository.LocationForecastRepository
import no.uio.ifi.in2000.team6.rakett_app.data.CoordinatesManager
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository
import no.uio.ifi.in2000.team6.rakett_app.data.repository.LaunchPointRepositoryInterface
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHourUIState

class HomeScreenViewModel(
    private val repository: SafetyReportRepository,
    private val launchPointRepository: LaunchPointRepositoryInterface? = null
) : ViewModel() {

    private val _temperatureState = MutableStateFlow(0.0)
    val temperatureState: StateFlow<Double> = _temperatureState.asStateFlow()

    private val _windSpeedState = MutableStateFlow(0.0)
    val windSpeedState: StateFlow<Double> = _windSpeedState.asStateFlow()

    private val _windDirectionState = MutableStateFlow(0.0)
    val windDirectionState: StateFlow<Double> = _windDirectionState.asStateFlow()

    private val _savedCoordinates = MutableStateFlow<List<Pair<Double, Double>>>(emptyList())
    val savedCoordinates: StateFlow<List<Pair<Double, Double>>> = _savedCoordinates.asStateFlow()

    private val _locationForecastRepository = LocationForecastRepository()

    private val _fourHourUIState = MutableStateFlow(FourHourUIState())
    val fourHourUIState = _fourHourUIState.asStateFlow()

    private val _launchPointState = MutableStateFlow(LaunchPointState())
    val launchPointState = _launchPointState.asStateFlow()

    init {
        // Observe the repository for any changes to launch points
        viewModelScope.launch {
            launchPointRepository?.getAllLaunchPoints()?.collectLatest { points ->
                _launchPointState.update {
                    it.copy(launchPoints = points)
                }

                // Update the selected location if there is one
                val selectedPoint = points.find { it.selected }
                if (selectedPoint != null) {
                    CoordinatesManager.updateLocation(selectedPoint.latitude, selectedPoint.longitude)
                    getFourHourForecast(selectedPoint.latitude, selectedPoint.longitude)
                }
            }
        }
    }

    fun updateSelectedLocation(state: LaunchPointState) {
        viewModelScope.launch(Dispatchers.IO) {
            _launchPointState.update {
                it.copy(
                    launchPoints = state.launchPoints
                )
            }
        }
    }


    fun selectLocation(launchPoint: LaunchPoint) {
        viewModelScope.launch(Dispatchers.IO) {
            // Update the database
            launchPointRepository?.deselectAllLaunchPoints()
            launchPointRepository?.updateLaunchPoint(launchPoint.copy(selected = true))

            // Update the coordinates manager with the new coordinates
            CoordinatesManager.updateLocation(launchPoint.latitude, launchPoint.longitude)

            // Fetch the forecast for the new location
            getFourHourForecast(launchPoint.latitude, launchPoint.longitude)

            // Also update the local state for immediate UI feedback
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
        }
    }


    fun getFourHourForecast(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val fourHourForecast = _locationForecastRepository.getNextFourHourForecast(latitude, longitude)

            _fourHourUIState.update {
                it.copy(
                    list = fourHourForecast
                )
            }
        }
    }
}
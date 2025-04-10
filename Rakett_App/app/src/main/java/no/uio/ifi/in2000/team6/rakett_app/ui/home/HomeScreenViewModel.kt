package no.uio.ifi.in2000.team6.rakett_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState
import no.uio.ifi.in2000.team6.rakett_app.data.repository.LocationForecastRepository
import no.uio.ifi.in2000.team6.rakett_app.data.CoordinatesManager
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHourUIState

class HomeScreenViewModel(
) : ViewModel() {
    // Vi bruker LocationForecastRepository direkte
    private val _locationForecastRepository = LocationForecastRepository()

    // State for værdata for de nærmeste 4 timene
    private val _fourHourUIState = MutableStateFlow(FourHourUIState())
    val fourHourUIState = _fourHourUIState.asStateFlow()

    fun updateSelectedLocation(state: LaunchPointState) {
        viewModelScope.launch(Dispatchers.IO) {
            // Oppdater koordinatene for valgt oppskytningssted
            val selectedPoint = state.launchPoints.find { it.selected }
            if (selectedPoint != null) {
                CoordinatesManager.updateLocation(selectedPoint.latitude, selectedPoint.longitude)
                // Sørg for at værdata oppdateres med de nye koordinatene
                getFourHourForecast(selectedPoint.latitude, selectedPoint.longitude)
            }
        }
    }

    fun getFourHourForecast(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fourHourForecast = _locationForecastRepository.getNextFourHourForecast(latitude, longitude)

                _fourHourUIState.update {
                    it.copy(
                        list = fourHourForecast
                    )
                }
            } catch (e: Exception) {
                // Håndter feil ved henting av værdata
                _fourHourUIState.update {
                    it.copy(
                        list = emptyList()
                    )
                }
            }
        }
    }
}
package no.uio.ifi.in2000.team6.rakett_app.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
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
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FiveDayUIState
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHourUIState

class HomeScreenViewModel(
    private val repository: SafetyReportRepository
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
    val fourHourUIState =  _fourHourUIState.asStateFlow()

    private val _launchPointState = MutableStateFlow(LaunchPointState())
    val launchPointState = _launchPointState.asStateFlow()

    fun updateSelectedLocation(state: LaunchPointState) {
        viewModelScope.launch(Dispatchers.IO) {

            _launchPointState.update {
                it.copy(
                    launchPoints = state.launchPoints
                )
            }
        }
    }


    fun getFourHourForecast(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val fourHourForecast = _locationForecastRepository.getNextFourHourForecast(latitude,longitude)

            _fourHourUIState.update {
                it.copy(
                    list = fourHourForecast
                )
            }
        }
    }


}

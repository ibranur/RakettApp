package no.uio.ifi.in2000.team6.rakett_app.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository

class HomeScreenViewModel(
    private val repository: SafetyReportRepository
) : ViewModel() {

    private val _temperatureState = MutableStateFlow(0.0)
    val temperatureState: StateFlow<Double> = _temperatureState.asStateFlow()

    private val _windSpeedState = MutableStateFlow(0.0)
    val windSpeedState: StateFlow<Double> = _windSpeedState.asStateFlow()

    private val _windDirectionState = MutableStateFlow(0.0)
    val windDirectionState: StateFlow<Double> = _windDirectionState.asStateFlow()

    init {
        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        viewModelScope.launch {
            try {
                val report = repository.getSafetyReport(59.9139, 10.7522)
                _temperatureState.value = report.air_temperature
                _windSpeedState.value = report.wind_speed
                _windDirectionState.value = report.wind_direction
            } catch (e: Exception) {
                println("ViewModel Fetch Error: ${e.message}")
                e.printStackTrace()
                // Set default values if fetch fails
                _temperatureState.value = 0.0
                _windSpeedState.value = 0.0
                _windDirectionState.value = 0.0
            }
        }
    }
}
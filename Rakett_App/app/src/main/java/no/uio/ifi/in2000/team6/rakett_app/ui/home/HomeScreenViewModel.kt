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
    init {
        fetchTemperature()
    }

    private fun fetchTemperature() {
        viewModelScope.launch {
            val report = repository.getSafetyReport(59.9139, 10.7522)
            _temperatureState.value = report.air_temperature

        }
    }
}
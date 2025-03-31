package no.uio.ifi.in2000.team6.rakett_app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CoordinatesManager {
    private val _currentLocation = MutableStateFlow<Pair<Double, Double>>(59.9139 to 10.7522) // Oslo
    val currentLocation: StateFlow<Pair<Double, Double>> = _currentLocation.asStateFlow()

    fun updateLocation(latitude: Double, longitude: Double) {
        _currentLocation.value = latitude to longitude
    }
}
package no.uio.ifi.in2000.team6.rakett_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LaunchPointViewModel(
    private val dao: LaunchPointDao
): ViewModel() {

    private val _state = MutableStateFlow(LaunchPointState())
    private val _launchPoints = dao.getAllLaunchPoints()

    val state = combine(_state, _launchPoints) { state, launchPoints ->
        state.copy(
            launchPoints = launchPoints
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LaunchPointState())


    fun onEvent(event: LaunchPointEvent) {
        when (event) {
            is LaunchPointEvent.DeleteLaunchPoint ->
                viewModelScope.launch {
                    dao.deleteLaunchPoint(event.launchPoint)
                }
            is LaunchPointEvent.setLatitude -> _state.update { it.copy(
                latitude = event.latitude
            )
            }
            is LaunchPointEvent.setLongitude -> _state.update { it.copy(
                longitude = event.longitude
            )
            }
            LaunchPointEvent.saveLaunchPoint -> {
                val latitude = state.value.latitude
                val longitude = state.value.longitude

                if (latitude == 0.0 || longitude == 0.0) {
                    return
                }

                val launchPoint = LaunchPoint(
                    latitude = latitude,
                    longitude = longitude
                )

                viewModelScope.launch {
                    dao.upsertLaunchPoint(launchPoint)
                }
                _state.update { it.copy(
                    isAddingLaunchPoint = false,
                    latitude = 0.0,
                    longitude = 0.0
                )
                }
            }

            LaunchPointEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingLaunchPoint = false
                ) }
            }

            LaunchPointEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingLaunchPoint = true
                ) }
            }
        }
    }
}
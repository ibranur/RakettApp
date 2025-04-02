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
            is LaunchPointEvent.setName -> _state.update { it.copy(
                name = event.name
            ) }
            LaunchPointEvent.saveLaunchPoint -> {
                val latitudeStr = state.value.latitude
                val longitudeStr = state.value.longitude

                val latitude = latitudeStr.toDoubleOrNull() ?: 0.0
                val longitude = longitudeStr.toDoubleOrNull() ?: 0.0
                val name = state.value.name

                if (name != null) {
                    val launchPoint = LaunchPoint(
                        latitude = latitude,
                        longitude = longitude,
                        name = name
                    )


                    viewModelScope.launch {
                        dao.upsertLaunchPoint(launchPoint)
                    }
                    _state.update {
                        it.copy(
                            isAddingLaunchPoint = false,
                            latitude = "",
                            longitude = "",
                            name = ""
                        )
                    }
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
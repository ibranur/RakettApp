package no.uio.ifi.in2000.team6.rakett_app.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team6.rakett_app.data.repository.LaunchPointRepositoryInterface
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState


class SavedLocationViewModel(
    private val repository: LaunchPointRepositoryInterface
) : ViewModel() {

    private val _state = MutableStateFlow(LaunchPointState())
    private val _launchPoints = repository.getAllLaunchPoints()

    // Kombinerer tilstand med data fra repository
    val state = combine(_state, _launchPoints) { state, launchPoints ->
        state.copy(
            launchPoints = launchPoints
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LaunchPointState())

    fun onEvent(event: LaunchPointEvent) {
        when (event) {
            // Håndterer sletting av oppskytningspunkt
            is LaunchPointEvent.DeleteLaunchPoint ->
                viewModelScope.launch {
                    repository.deleteLaunchPoint(event.launchPoint)
                }

            // Oppdaterer breddegrad i tilstand
            is LaunchPointEvent.setLatitude -> _state.update {
                it.copy(
                    latitude = event.latitude
                )
            }

            // Oppdaterer lengdegrad i tilstand
            is LaunchPointEvent.setLongitude -> _state.update {
                it.copy(
                    longitude = event.longitude
                )
            }

            // Oppdaterer navn i tilstand
            is LaunchPointEvent.setName -> _state.update {
                it.copy(
                    name = event.name
                )
            }

            // Oppdaterer oppskytningspunkt i databasen
            is LaunchPointEvent.UpdateLaunchPoint -> viewModelScope.launch {
                repository.deselectAllLaunchPoints()
                repository.updateLaunchPoint(event.launchPoint)
            }

            // Lagrer nytt oppskytningspunkt
            LaunchPointEvent.saveLaunchPoint -> {
                val latitudeStr = _state.value.latitude
                val longitudeStr = _state.value.longitude
                val name = _state.value.name

                val latitude = latitudeStr.toDoubleOrNull() ?: 0.0
                val longitude = longitudeStr.toDoubleOrNull() ?: 0.0

                if (name.isNotBlank()) {
                    val launchPoint = LaunchPoint(
                        latitude = latitude,
                        longitude = longitude,
                        name = name,
                        selected = false
                    )

                    viewModelScope.launch {
                        repository.upsertLaunchPoint(launchPoint)
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

            // Skjuler dialogboks
            LaunchPointEvent.HideDialog -> {
                _state.update {
                    it.copy(
                        isAddingLaunchPoint = false
                    )
                }
            }

            // Viser dialogboks
            LaunchPointEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        isAddingLaunchPoint = true
                    )
                }
            }

            // Veksler oppdateringsdialog
            LaunchPointEvent.ToggleUpdateDialog -> {
                _state.update {
                    it.copy(
                        isUpdatingLaunchPoint = !it.isUpdatingLaunchPoint,
                    )
                }
            }
        }
    }
}
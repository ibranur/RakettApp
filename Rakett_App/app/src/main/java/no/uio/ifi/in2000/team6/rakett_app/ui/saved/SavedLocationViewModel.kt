package no.uio.ifi.in2000.team6.rakett_app.ui.saved

import android.util.Log
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
): ViewModel() {
    private val TAG = "SavedLocationViewModel"

    private val _state = MutableStateFlow(LaunchPointState())
    private val _launchPoints = repository.getAllLaunchPoints()

    val state = combine(_state, _launchPoints) { state, launchPoints ->
        Log.d(TAG, "State updated: ${launchPoints.size} locations available")
        state.copy(
            launchPoints = launchPoints
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LaunchPointState())

    fun onEvent(event: LaunchPointEvent) {
        Log.d(TAG, "Received event: $event")

        when (event) {
            is LaunchPointEvent.DeleteLaunchPoint -> {
                Log.d(TAG, "Deleting location: ${event.launchPoint.name}")
                viewModelScope.launch {
                    // Check if this is a selected point and we need to select another
                    val isSelected = event.launchPoint.selected
                    val otherPoints = state.value.launchPoints.filter { it.id != event.launchPoint.id }

                    if (isSelected && otherPoints.isNotEmpty()) {
                        // Find another location to select first
                        val otherPoint = otherPoints.first()
                        Log.d(TAG, "Selected point deleted, selecting new point: ${otherPoint.name}")
                        repository.updateLaunchPoint(otherPoint.copy(selected = true))
                    }

                    // Now delete the point
                    repository.deleteLaunchPoint(event.launchPoint)
                    Log.d(TAG, "Location deleted successfully")
                }
            }

            is LaunchPointEvent.setLatitude -> {
                Log.d(TAG, "Setting latitude: ${event.latitude}")
                _state.update { it.copy(
                    latitude = event.latitude
                )}
            }

            is LaunchPointEvent.setLongitude -> {
                Log.d(TAG, "Setting longitude: ${event.longitude}")
                _state.update { it.copy(
                    longitude = event.longitude
                )}
            }

            is LaunchPointEvent.setName -> {
                Log.d(TAG, "Setting name: ${event.name}")
                _state.update { it.copy(
                    name = event.name
                )}
            }

            is LaunchPointEvent.UpdateLaunchPoint -> {
                viewModelScope.launch {
                    Log.d(TAG, "Updating launch point: ${event.launchPoint.name}, selected=${event.launchPoint.selected}")
                    if (event.launchPoint.selected) {
                        repository.deselectAllLaunchPoints()
                    }
                    repository.updateLaunchPoint(event.launchPoint)
                    Log.d(TAG, "Location updated successfully: ${event.launchPoint.name}")
                }
            }

            LaunchPointEvent.saveLaunchPoint -> {
                // Handle adding a new location
                val latitudeStr = state.value.latitude
                val longitudeStr = state.value.longitude
                val name = state.value.name

                Log.d(TAG, "Attempting to save launch point - lat: $latitudeStr, lon: $longitudeStr, name: $name")

                val latitude = latitudeStr.toDoubleOrNull()
                val longitude = longitudeStr.toDoubleOrNull()

                if (latitude != null && longitude != null && name.isNotBlank()) {
                    val isFirstLocation = state.value.launchPoints.isEmpty()
                    Log.d(TAG, "Validation passed, saving new location. Is first location: $isFirstLocation")

                    viewModelScope.launch {
                        try {
                            // If this is not the first location, deselect all others first
                            if (!isFirstLocation && true) { // Set to true to select the new point automatically
                                repository.deselectAllLaunchPoints()
                            }

                            // For the first location we add, make it selected
                            val newLocation = LaunchPoint(
                                latitude = latitude,
                                longitude = longitude,
                                name = name,
                                selected = true // Always select the newly added location
                            )

                            repository.upsertLaunchPoint(newLocation)
                            Log.d(TAG, "New location saved successfully: $name")

                            // Reset form fields and hide dialog
                            _state.update {
                                it.copy(
                                    isAddingLaunchPoint = false,
                                    latitude = "",
                                    longitude = "",
                                    name = ""
                                )
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error saving location", e)
                        }
                    }
                } else {
                    Log.e(TAG, "Invalid location data: name=$name, lat=$latitude, long=$longitude")
                }
            }

            is LaunchPointEvent.SetCurrentEditLocation -> {
                val location = event.launchPoint
                _state.update {
                    it.copy(
                        currentEditLocation = location,
                        latitude = location.latitude.toString(),
                        longitude = location.longitude.toString(),
                        name = location.name
                    )
                }
            }

            LaunchPointEvent.ShowEditDialog -> {
                _state.update {
                    it.copy(isEditingLaunchPoint = true)
                }
            }

            LaunchPointEvent.HideEditDialog -> {
                _state.update {
                    it.copy(
                        isEditingLaunchPoint = false,
                        currentEditLocation = null
                    )
                }
            }

            LaunchPointEvent.ShowDialog -> {
                Log.d(TAG, "Showing add location dialog")
                _state.update { it.copy(
                    isAddingLaunchPoint = true,
                    // Clear form fields when showing the dialog
                    latitude = "",
                    longitude = "",
                    name = ""
                )}
            }

            LaunchPointEvent.HideDialog -> {
                Log.d(TAG, "Hiding add location dialog")
                _state.update { it.copy(
                    isAddingLaunchPoint = false
                )}
            }
        }
    }
}
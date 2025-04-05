package no.uio.ifi.in2000.team6.rakett_app.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState

@Composable
fun EditLocationDialog(
    state: LaunchPointState,
    onEvent: (LaunchPointEvent) -> Unit
) {
    val currentLocation = state.currentEditLocation ?: return

    var latitudeError by remember { mutableStateOf(false) }
    var longitudeError by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    // Function to validate input
    fun validateInput(): Boolean {
        val latIsValid = state.latitude.isNotBlank() && state.latitude.toDoubleOrNull() != null
        val longIsValid = state.longitude.isNotBlank() && state.longitude.toDoubleOrNull() != null
        val nameIsValid = state.name.isNotBlank()

        latitudeError = !latIsValid
        longitudeError = !longIsValid
        nameError = !nameIsValid

        return latIsValid && longIsValid && nameIsValid
    }

    // Function to save edited location
    fun saveEditedLocation() {
        if (validateInput()) {
            val latitude = state.latitude.toDoubleOrNull() ?: return
            val longitude = state.longitude.toDoubleOrNull() ?: return
            val name = state.name

            val updatedLocation = currentLocation.copy(
                latitude = latitude,
                longitude = longitude,
                name = name
            )

            onEvent(LaunchPointEvent.UpdateLaunchPoint(updatedLocation))
            onEvent(LaunchPointEvent.HideEditDialog)
        }
    }

    AlertDialog(
        onDismissRequest = { onEvent(LaunchPointEvent.HideEditDialog) },
        title = { Text("Rediger lokasjon") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = state.latitude,
                    onValueChange = {
                        onEvent(LaunchPointEvent.setLatitude(it))
                        latitudeError = false
                    },
                    label = { Text("Latitude") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = latitudeError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                TextField(
                    value = state.longitude,
                    onValueChange = {
                        onEvent(LaunchPointEvent.setLongitude(it))
                        longitudeError = false
                    },
                    label = { Text("Longitude") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = longitudeError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                TextField(
                    value = state.name,
                    onValueChange = {
                        onEvent(LaunchPointEvent.setName(it))
                        nameError = false
                    },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError
                )
            }
        },
        confirmButton = {
            Button(onClick = { saveEditedLocation() }) {
                Text("Lagre endringer")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onEvent(LaunchPointEvent.HideEditDialog) }) {
                Text("Avbryt")
            }
        }
    )
}
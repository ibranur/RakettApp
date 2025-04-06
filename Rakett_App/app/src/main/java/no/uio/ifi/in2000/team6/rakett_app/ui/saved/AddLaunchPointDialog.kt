package no.uio.ifi.in2000.team6.rakett_app.ui.saved

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState

@Composable
fun AddLaunchPointDialog(
    state: LaunchPointState,
    onEvent: (LaunchPointEvent) -> Unit
) {
    var latitudeError by remember { mutableStateOf(false) }
    var longitudeError by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    // To auto-focus the first field
    val focusRequester = remember { FocusRequester() }

    DisposableEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (e: Exception) {
            Log.e("AddLaunchPointDialog", "Error requesting focus", e)
        }

        onDispose { }
    }

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

    Dialog(
        onDismissRequest = { onEvent(LaunchPointEvent.HideDialog) },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fjernet tittel "Legg til lokasjon" som ønsket

            Spacer(modifier = Modifier.height(8.dp))

            // Form fields
            TextField(
                value = state.latitude,
                onValueChange = {
                    onEvent(LaunchPointEvent.setLatitude(it))
                    latitudeError = false
                },
                label = { Text("Latitude") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                isError = latitudeError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                supportingText = {
                    if (latitudeError) {
                        Text("Vennligst oppgi en gyldig latitude")
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = state.longitude,
                onValueChange = {
                    onEvent(LaunchPointEvent.setLongitude(it))
                    longitudeError = false
                },
                label = { Text("Longitude") },
                modifier = Modifier.fillMaxWidth(),
                isError = longitudeError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                supportingText = {
                    if (longitudeError) {
                        Text("Vennligst oppgi en gyldig longitude")
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextField(
                value = state.name,
                onValueChange = {
                    onEvent(LaunchPointEvent.setName(it))
                    nameError = false
                },
                label = { Text("Stedsnavn") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text("Vennligst oppgi et navn")
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = { onEvent(LaunchPointEvent.HideDialog) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Avbryt")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (validateInput()) {
                            onEvent(LaunchPointEvent.saveLaunchPoint)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Lagre")
                }
            }
        }
    }
}
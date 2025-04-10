package no.uio.ifi.in2000.team6.rakett_app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.utils.CoordinateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLaunchSiteDialog(
    onEvent: (LaunchPointEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Legg til lokasjon",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Navn") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = hasError && name.isBlank()
                )

                TextField(
                    value = latitude,
                    onValueChange = {
                        latitude = it
                        hasError = !CoordinateUtils.validateLatitude(it) && it.isNotBlank()
                        if (hasError) errorMessage = "Latitude må være mellom -90 og 90"
                    },
                    label = { Text("Latitude") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = hasError && (latitude.isBlank() || !CoordinateUtils.validateLatitude(latitude))
                )

                TextField(
                    value = longitude,
                    onValueChange = {
                        longitude = it
                        hasError = !CoordinateUtils.validateLongitude(it) && it.isNotBlank()
                        if (hasError) errorMessage = "Longitude må være mellom -180 og 180"
                    },
                    label = { Text("Longitude") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = hasError && (longitude.isBlank() || !CoordinateUtils.validateLongitude(longitude))
                )

                if (hasError) {
                    Text(
                        text = errorMessage.ifEmpty { "Alle felt må fylles ut korrekt" },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (name.isBlank() ||
                                !CoordinateUtils.validateLatitude(latitude) ||
                                !CoordinateUtils.validateLongitude(longitude)) {
                                hasError = true
                                errorMessage = "Alle felt må fylles ut korrekt"
                                return@Button
                            }

                            // Send hendelser direkte for å legge til oppskytningssted
                            onEvent(LaunchPointEvent.setName(name))
                            onEvent(LaunchPointEvent.setLatitude(latitude))
                            onEvent(LaunchPointEvent.setLongitude(longitude))
                            onEvent(LaunchPointEvent.saveLaunchPoint)
                            onDismiss()
                        }
                    ) {
                        Text("Lagre")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditLaunchSiteDialog(
    launchPoint: LaunchPoint,
    onEvent: (LaunchPointEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(launchPoint.name) }
    var latitude by remember { mutableStateOf(launchPoint.latitude.toString()) }
    var longitude by remember { mutableStateOf(launchPoint.longitude.toString()) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Rediger oppskytningssted",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Navn") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = hasError && name.isBlank()
                )

                TextField(
                    value = latitude,
                    onValueChange = {
                        latitude = it
                        hasError = !CoordinateUtils.validateLatitude(it) && it.isNotBlank()
                        if (hasError) errorMessage = "Latitude må være mellom -90 og 90"
                    },
                    label = { Text("Latitude") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = hasError && !CoordinateUtils.validateLatitude(latitude)
                )

                TextField(
                    value = longitude,
                    onValueChange = {
                        longitude = it
                        hasError = !CoordinateUtils.validateLongitude(it) && it.isNotBlank()
                        if (hasError) errorMessage = "Longitude må være mellom -180 og 180"
                    },
                    label = { Text("Longitude") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = hasError && !CoordinateUtils.validateLongitude(longitude)
                )

                if (hasError) {
                    Text(
                        text = errorMessage.ifEmpty { "Alle felt må fylles ut korrekt" },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (name.isBlank() ||
                                !CoordinateUtils.validateLatitude(latitude) ||
                                !CoordinateUtils.validateLongitude(longitude)) {
                                hasError = true
                                errorMessage = "Alle felt må fylles ut korrekt"
                                return@Button
                            }

                            // Oppdater eksisterende oppskytningssted
                            val updatedLaunchPoint = launchPoint.copy(
                                name = name,
                                latitude = latitude.toDouble(),
                                longitude = longitude.toDouble()
                            )

                            onEvent(LaunchPointEvent.UpdateLaunchPoint(updatedLaunchPoint))
                            onDismiss()
                        }
                    ) {
                        Text("Lagre endringer")
                    }
                }
            }
        }
    }
}
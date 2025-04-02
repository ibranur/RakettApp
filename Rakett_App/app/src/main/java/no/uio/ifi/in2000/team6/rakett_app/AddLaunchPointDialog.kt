package no.uio.ifi.in2000.team6.rakett_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp


@Composable
fun AddLaunchPointDialog(
    state: LaunchPointState,
    onEvent: (LaunchPointEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(LaunchPointEvent.HideDialog)
        },
        title = { Text(text = "Add launchpoint") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(value = state.latitude.toString(),
                    onValueChange = { newText ->
                        onEvent(LaunchPointEvent.setLatitude(newText))
                    },
                    placeholder = {Text(text = "Latitude")}
                )
                TextField(value = state.longitude.toString(),
                    onValueChange = { newText ->
                        onEvent(LaunchPointEvent.setLongitude(newText))
                    },
                    placeholder = {Text(text = "Longitude")}
                )
            }
        },
        confirmButton = {
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
                ) {
                Button(
                    onClick = {
                        onEvent(LaunchPointEvent.saveLaunchPoint)
                    }
                ) {
                    Text(text = "Save launchpoint")
                }
            }
        }
    )
}



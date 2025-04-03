package no.uio.ifi.in2000.team6.rakett_app.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLaunchPointDialog(
    state: LaunchPointState,
    onEvent: (LaunchPointEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicAlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onEvent(LaunchPointEvent.HideDialog)
        },
        content = { AlertContent(state, onEvent) }
    )

}


@Composable
fun AlertContent(
    state: LaunchPointState,
    onEvent: (LaunchPointEvent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = state.latitude,
            onValueChange = { newText ->
                onEvent(LaunchPointEvent.setLatitude(newText))
            },
            placeholder = { Text(text = "Latitude") }
        )
        TextField(
            value = state.longitude,
            onValueChange = { newText ->
                onEvent(LaunchPointEvent.setLongitude(newText))
            },
            placeholder = { Text(text = "Longitude") }
        )
        TextField(
            value = state.name,
            onValueChange = { newText ->
                onEvent(LaunchPointEvent.setName(newText))
            },
            placeholder = { Text(text = "Name") }
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
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
}
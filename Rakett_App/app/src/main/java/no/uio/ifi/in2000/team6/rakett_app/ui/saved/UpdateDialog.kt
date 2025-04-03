package no.uio.ifi.in2000.team6.rakett_app.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
fun UpdateDialog(state: LaunchPointState, onEvent: (LaunchPointEvent) -> Unit) {
    BasicAlertDialog(
        onDismissRequest = {onEvent(LaunchPointEvent.ToggleUpdateDialog)},
        content ={ ContentForUpdate(state,onEvent) }
    )
}

@Composable
fun ContentForUpdate(
    state: LaunchPointState,
    onEvent: (LaunchPointEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(value = state.name,
            onValueChange = { newText ->
                onEvent(LaunchPointEvent.setName(newText))
            },
            placeholder = { Text(text = "Name") }
        )

        Box(modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
//                    onEvent()
                }
            ) {
                Text(text = "Save")
            }
        }
    }
}
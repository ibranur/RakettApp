package no.uio.ifi.in2000.team6.rakett_app.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.CoordCard

@Composable
fun SavedLocationScreen(state: LaunchPointState,
                        onEvent: (LaunchPointEvent) -> Unit) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onEvent(LaunchPointEvent.ShowDialog)
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add launch point"
                )
            }
        }) { padding ->

        if (state.isAddingLaunchPoint) {
            AddLaunchPointDialog(state = state, onEvent = onEvent)
        }

        if (state.isUpdatingLaunchPoint) {
            UpdateDialog(state,onEvent)
        }

        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.launchPoints) { launchPoint ->
                CoordCard(
                    launchPoint = launchPoint,
                    onClick = onEvent,
                )
            }
        }
    }
}

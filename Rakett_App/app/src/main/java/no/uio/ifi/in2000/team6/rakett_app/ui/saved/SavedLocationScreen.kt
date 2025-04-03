package no.uio.ifi.in2000.team6.rakett_app.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.CoordCard
import no.uio.ifi.in2000.team6.rakett_app.ui.elements.TopBar

@Composable
fun SavedLocationScreen(
    state: LaunchPointState,
    onEvent: (LaunchPointEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Oppskytningssteder",
                isEmpty = state.launchPoints.isEmpty(),
                actions = {
                    IconButton(onClick = { /* Help or info action */ }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Information"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(LaunchPointEvent.ShowDialog) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add launch point"
                )
            }
        }
    ) { padding ->
        if (state.isAddingLaunchPoint) {
            AddLaunchPointDialog(state = state, onEvent = onEvent)
        }

        if (state.isUpdatingLaunchPoint) {
            UpdateDialog(state, onEvent)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.launchPoints.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Ingen lagrede oppskytningssteder",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Trykk på plussknappen for å legge til",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.launchPoints) { launchPoint ->
                        CoordCard(
                            launchPoint = launchPoint,
                            onClick = onEvent
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SavedLocationScreenEmptyPreview() {
    // Create a sample state with no launch points
    val sampleState = LaunchPointState(
        launchPoints = emptyList(),
        latitude = "",
        longitude = "",
        name = "",
        isAddingLaunchPoint = false,
        isUpdatingLaunchPoint = false
    )

    // Preview empty state
    SavedLocationScreen(
        state = sampleState,
        onEvent = { /* No action needed for preview */ }
    )
}

@Preview(showBackground = true)
@Composable
fun SavedLocationScreenPopulatedPreview() {
    // Sample launch points for populated preview
    val sampleLaunchPoints = listOf(
        LaunchPoint(
            latitude = 59.9139,
            longitude = 10.7522,
            name = "Oslo",
            selected = false,
            id = 1
        ),
        LaunchPoint(
            latitude = 60.3913,
            longitude = 5.3221,
            name = "Bergen",
            selected = true,
            id = 2
        ),
        LaunchPoint(
            latitude = 63.4305,
            longitude = 10.3951,
            name = "Trondheim",
            selected = false,
            id = 3
        )
    )

    // Create a sample state with launch points
    val sampleState = LaunchPointState(
        launchPoints = sampleLaunchPoints,
        latitude = "",
        longitude = "",
        name = "",
        isAddingLaunchPoint = false,
        isUpdatingLaunchPoint = false
    )

    // Preview populated state
    SavedLocationScreen(
        state = sampleState,
        onEvent = { /* No action needed for preview */ }
    )
}
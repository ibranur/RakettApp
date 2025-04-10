package no.uio.ifi.in2000.team6.rakett_app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.AltitudeWeatherSection
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.ExpandableCard

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel,
    gribViewModel: GribViewModel,
    state: LaunchPointState,
    onEvent: (LaunchPointEvent) -> Unit
) {
    // Dialog-tilstander
    var showAddDialog by remember { mutableStateOf(false) }
    var editingLaunchPoint by remember { mutableStateOf<LaunchPoint?>(null) }

    // Værdata-tilstander
    val fourHourUIState by viewModel.fourHourUIState.collectAsState()

    // Oppdater værdata når et nytt oppskytningssted velges
    LaunchedEffect(state.launchPoints) {
        val selectedPoint = state.launchPoints.find { it.selected }
        if (selectedPoint != null) {
            viewModel.getFourHourForecast(selectedPoint.latitude, selectedPoint.longitude)
            gribViewModel.fetchGribData(selectedPoint.latitude, selectedPoint.longitude)
            viewModel.updateSelectedLocation(state)
        }
    }

    // Grib-data
    val gribMaps by gribViewModel.gribMaps.collectAsState()
    val windShearValues by gribViewModel.windShearValues.collectAsState()
    val isLoadingGrib by gribViewModel.isLoading.collectAsState()
    val errorMessage by gribViewModel.errorMessage.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 0.dp) // No bottom padding
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown for valg av oppskytningssted
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                LaunchSiteDropdown(
                    state = state,
                    onEvent = onEvent,
                    onShowAddDialog = { showAddDialog = true },
                    onShowEditDialog = { launchPoint -> editingLaunchPoint = launchPoint }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Værdata for de neste 4 timene på bakkenivå
            Text(
                text = "Været på bakkenivå de neste 4 timene",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Viser værdata for de neste 4 timene
        if (fourHourUIState.list.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.launchPoints.isEmpty())
                            "Legg til et oppskytningssted for å se værdata"
                        else if (state.launchPoints.none { it.selected })
                            "Velg et oppskytningssted for å se værdata"
                        else
                            "Laster værdata...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            itemsIndexed(
                items = fourHourUIState.list.filterNotNull(),
                key = { index, item -> "forecast-${item.hour}-$index" }
            ) { _, fourHour ->
                ExpandableCard(fourHour = fourHour)
                Spacer(modifier = Modifier.height(3.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            AltitudeWeatherSection(
                modifier = Modifier.fillMaxWidth(),
                gribMaps = gribMaps,
                windShearValues = windShearValues,
                isLoading = isLoadingGrib,
                errorMessage = errorMessage,
                title = "Værdata i høyden nå"
            )

        }
    }

    // Dialoger for å legge til og redigere oppskytningssteder
    if (showAddDialog) {
        AddLaunchSiteDialog(
            onEvent = onEvent,
            onDismiss = { showAddDialog = false }
        )
    }

    editingLaunchPoint?.let { launchPoint ->
        EditLaunchSiteDialog(
            launchPoint = launchPoint,
            onEvent = onEvent,
            onDismiss = { editingLaunchPoint = null }
        )
    }
}
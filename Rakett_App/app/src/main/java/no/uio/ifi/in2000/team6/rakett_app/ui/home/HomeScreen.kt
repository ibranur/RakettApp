package no.uio.ifi.in2000.team6.rakett_app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.AltitudeWeatherSection
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.ExpandableCard
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import no.uio.ifi.in2000.team6.rakett_app.ui.saved.AddLaunchPointDialog
import no.uio.ifi.in2000.team6.rakett_app.ui.saved.EditLocationDialog
import androidx.compose.material3.HorizontalDivider

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel,
    gribViewModel: GribViewModel,
    navController: NavController? = null,
    state: LaunchPointState? = null,
    onEvent: ((LaunchPointEvent) -> Unit)? = null
) {
    val temperature by viewModel.temperatureState.collectAsState()
    val windSpeed by viewModel.windSpeedState.collectAsState()
    val windDirection by viewModel.windDirectionState.collectAsState()

    // Weather forecast
    val fourHourUIState by viewModel.fourHourUIState.collectAsState()

    // Get the current state of launch points
    val launchPointState by viewModel.launchPointState.collectAsState()

    // Grib data
    val gribMaps by gribViewModel.gribMaps.collectAsState()
    val windShearValues by gribViewModel.windShearValues.collectAsState()
    val isLoadingGrib by gribViewModel.isLoading.collectAsState()
    val errorMessage by gribViewModel.errorMessage.collectAsState()

    // State for location edit dialog
    var showEditDialog by remember { mutableStateOf(false) }
    var locationToEdit by remember { mutableStateOf<LaunchPoint?>(null) }

    // Remember the selected point for side effects
    val selectedPoint = launchPointState.launchPoints.find { it.selected }

    // Update GRIB data when selected point changes
    LaunchedEffect(selectedPoint) {
        if (selectedPoint != null) {
            gribViewModel.fetchGribData(selectedPoint.latitude, selectedPoint.longitude)
        }
    }

    // Handle showing add location dialog directly from state
    if (state?.isAddingLaunchPoint == true && onEvent != null) {
        AddLaunchPointDialog(
            state = state,
            onEvent = onEvent
        )
    }

    // Handle showing edit dialog
    if (showEditDialog && locationToEdit != null && onEvent != null) {
        // Set up form data first
        LaunchedEffect(locationToEdit) {
            onEvent(LaunchPointEvent.SetCurrentEditLocation(locationToEdit!!))
            onEvent(LaunchPointEvent.ShowEditDialog)
        }

        EditLocationDialog(
            state = state ?: LaunchPointState(),
            onEvent = { event ->
                onEvent(event)
                if (event is LaunchPointEvent.HideEditDialog) {
                    showEditDialog = false
                    locationToEdit = null
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Location dropdown at the top
        LocationDropdown(
            launchPoints = launchPointState.launchPoints,
            onLocationSelected = { location ->
                viewModel.selectLocation(location)
            },
            onAddLocation = {
                onEvent?.invoke(LaunchPointEvent.ShowDialog)
            },
            onDeleteLocation = { location ->
                onEvent?.invoke(LaunchPointEvent.DeleteLaunchPoint(location))
            },
            onEditLocation = { location ->
                locationToEdit = location
                showEditDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Været på bakkenivå de neste 4 timene",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Display weather data
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            items(fourHourUIState.list) { fourHour ->
                if (fourHour != null) {
                    ExpandableCard(
                        fourHour = fourHour,
                    )
                }
            }
        }

        //høydevind
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

@Composable
fun LocationDropdown(
    launchPoints: List<LaunchPoint>,
    onLocationSelected: (LaunchPoint) -> Unit,
    onAddLocation: () -> Unit,
    onDeleteLocation: (LaunchPoint) -> Unit,
    onEditLocation: (LaunchPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var locationToDelete by remember { mutableStateOf<LaunchPoint?>(null) }

    // Find selected location
    val selectedLocation = launchPoints.find { it.selected }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Button width - we'll match the dropdown to this
        val buttonWidthPercent = 0.9f

        // Location selection button
        Button(
            onClick = { expanded = true },
            modifier = Modifier
                .fillMaxWidth(buttonWidthPercent)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Center the location text
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedLocation?.name ?: "Velg lokasjon",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Åpne meny",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Dropdown menu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(buttonWidthPercent)
                .background(MaterialTheme.colorScheme.surface)
                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
        ) {
            // Add location option
            DropdownMenuItem(
                text = {
                    Text(
                        "Legg til ny lokasjon",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Legg til",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                onClick = {
                    expanded = false
                    onAddLocation()
                },
                colors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.primary
                )
            )

            // Divider with title
            if (launchPoints.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LAGREDE LOKASJONER",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 4.dp)
                )
            }

            // List of locations
            launchPoints.forEach { location ->
                val isSelected = location.selected

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable {
                            onLocationSelected(location)
                            expanded = false
                        }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Check icon for selected location
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Valgt",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(20.dp))
                    }

                    // Location name
                    Text(
                        text = location.name,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                    )

                    // Action buttons
                    Row {
                        // Edit button
                        IconButton(
                            onClick = {
                                expanded = false
                                onEditLocation(location)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Rediger",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Delete button
                        IconButton(
                            onClick = {
                                locationToDelete = location
                                showDeleteConfirmation = true
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Slett",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation && locationToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
                locationToDelete = null
            },
            title = { Text("Bekreft sletting") },
            text = { Text("Er du sikker på at du vil slette lokasjonen '${locationToDelete?.name}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        locationToDelete?.let { onDeleteLocation(it) }
                        showDeleteConfirmation = false
                        locationToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Slett")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showDeleteConfirmation = false
                        locationToDelete = null
                    }
                ) {
                    Text("Avbryt")
                }
            }
        )
    }
}
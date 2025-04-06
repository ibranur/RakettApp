package no.uio.ifi.in2000.team6.rakett_app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import no.uio.ifi.in2000.team6.rakett_app.ui.saved.EditLocationDialog
import android.util.Log
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel,
    gribViewModel: GribViewModel,
    navController: NavController? = null,
    state: LaunchPointState? = null,
    onEvent: ((LaunchPointEvent) -> Unit)? = null
) {
    val tag = "HomeScreen"
    Log.d(tag, "Rendering HomeScreen with state: $state")

    val scope = rememberCoroutineScope()

    // Weather forecast
    val fourHourUIState by viewModel.fourHourUIState.collectAsState()

    // Selected location from ViewModel
    val selectedLocationFromViewModel by viewModel.selectedLocation.collectAsState()

    // Get the current state of launch points
    val launchPointState by viewModel.launchPointState.collectAsState()
    Log.d(tag, "Launch points from view model: ${launchPointState.launchPoints.size}")

    // Grib data
    val gribMaps by gribViewModel.gribMaps.collectAsState()
    val windShearValues by gribViewModel.windShearValues.collectAsState()
    val isLoadingGrib by gribViewModel.isLoading.collectAsState()
    val errorMessage by gribViewModel.errorMessage.collectAsState()

    // State for dropdown
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Remember the selected point from state
    val selectedPoint = state?.launchPoints?.find { it.selected }

    // Debugging location info
    LaunchedEffect(selectedPoint) {
        Log.d(tag, "Current selected point: ${selectedPoint?.name}")
    }

    // Sync GRIB data when selected point changes
    LaunchedEffect(selectedPoint) {
        Log.d(tag, "LaunchedEffect for selectedPoint: ${selectedPoint?.name}")
        if (selectedPoint != null) {
            // Clear existing GRIB data first
            gribViewModel.clearData()
            // Then fetch new data, passing location name for debugging
            gribViewModel.fetchGribData(
                selectedPoint.latitude,
                selectedPoint.longitude,
                selectedPoint.name
            )
        }
    }

    // Handle showing add location dialog directly from state
    if (state?.isAddingLaunchPoint == true && onEvent != null) {
        Log.d(tag, "Showing add location dialog")
        no.uio.ifi.in2000.team6.rakett_app.ui.saved.AddLaunchPointDialog(
            state = state,
            onEvent = onEvent
        )
    }

    // Handle showing edit dialog
    if (state?.isEditingLaunchPoint == true && state.currentEditLocation != null && onEvent != null) {
        Log.d(tag, "Showing edit dialog for: ${state.currentEditLocation.name}")
        EditLocationDialog(
            state = state,
            onEvent = onEvent
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Location dropdown at the top
        if (onEvent != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Location Button showing the currently selected location
                Button(
                    onClick = { dropdownExpanded = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE3E8FD),
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Center the location text - using selectedPoint to immediately show changes
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                // Use the ViewModel's selectedLocation which is updated immediately
                                text = selectedLocationFromViewModel?.name ?: "Velg lokasjon",
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
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 8.dp)
                ) {
                    // Add location option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                dropdownExpanded = false
                                onEvent(LaunchPointEvent.ShowDialog)
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Legg til",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "Legg til ny lokasjon",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Divider with title
                    if (state?.launchPoints?.isNotEmpty() == true) {
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
                                .padding(top = 4.dp, bottom = 8.dp)
                        )

                        // List of locations
                        state.launchPoints.forEach { location ->
                            val isSelected = location.id == selectedLocationFromViewModel?.id

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clickable {
                                        Log.d(tag, "Selected location: ${location.name}")

                                        // Close dropdown first
                                        dropdownExpanded = false

                                        // Now select the location - this will update UI immediately
                                        scope.launch {
                                            // Clear GRIB data first
                                            gribViewModel.clearData()

                                            // Select location in ViewModel
                                            viewModel.selectLocation(location)

                                            // Fetch GRIB data for the new location
                                            gribViewModel.fetchGribData(
                                                location.latitude,
                                                location.longitude,
                                                location.name
                                            )
                                        }
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
                                            Log.d(tag, "Edit location: ${location.name}")
                                            dropdownExpanded = false
                                            onEvent(LaunchPointEvent.SetCurrentEditLocation(location))
                                            onEvent(LaunchPointEvent.ShowEditDialog)
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
                                            Log.d(tag, "Delete button clicked for: ${location.name}")
                                            onEvent(LaunchPointEvent.DeleteLaunchPoint(location))
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
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weather data sections
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Ground level weather - takes approximately 60% of available space
            Column(
                modifier = Modifier.weight(0.60f)
            ) {
                // Title row without refresh button
                Text(
                    text = "Været på bakkenivå de neste 4 timene",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (selectedLocationFromViewModel == null) {
                    // No location selected message
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Legg til og velg en lokasjon for å se værdata",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (fourHourUIState.list.isEmpty()) {
                    // Loading indicator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Display 4-hour weather data in a fixed container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            items(fourHourUIState.list) { fourHour ->
                                if (fourHour != null) {
                                    ExpandableCard(fourHour = fourHour)
                                }
                            }
                        }
                    }
                }
            }

            // Divider to separate the two weather sections
            Spacer(modifier = Modifier.height(8.dp))

            // Altitude weather data - takes approximately 40% of available space
            Column(
                modifier = Modifier.weight(0.40f)
            ) {
                Text(
                    text = "Værdata i høyden nå",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (selectedLocationFromViewModel == null) {
                    // No location selected
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Legg til og velg en lokasjon for å se værdata",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Display altitude weather data
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        AltitudeWeatherSection(
                            modifier = Modifier.fillMaxWidth(),
                            gribMaps = gribMaps,
                            windShearValues = windShearValues,
                            isLoading = isLoadingGrib,
                            errorMessage = errorMessage,
                            title = ""
                        )
                    }
                }
            }
        }
    }
}
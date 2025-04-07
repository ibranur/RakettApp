package no.uio.ifi.in2000.team6.rakett_app.ui

import StartScreenViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState
import no.uio.ifi.in2000.team6.rakett_app.ui.saved.SavedLocationScreen
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository
import no.uio.ifi.in2000.team6.rakett_app.ui.home.GribViewModel
import no.uio.ifi.in2000.team6.rakett_app.ui.home.HomeScreen
import no.uio.ifi.in2000.team6.rakett_app.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.team6.rakett_app.ui.start.StartScreen
import android.util.Log

sealed class Screen(val route: String, val label: String) {
    data object Home : Screen("home", "Oppskytningssted")
    data object Start : Screen("start", "GO-VINDU")
    data object Saved : Screen("saved", "Lagret")
}

@Composable
fun Navigation(
    state: LaunchPointState,
    onEvent: (LaunchPointEvent) -> Unit
) {
    val navController = rememberNavController()
    val tag = "Navigation"
    val scope = rememberCoroutineScope()

    // Create repositories and ViewModels - use remember to preserve them across recompositions
    val safetyReportRepository = remember { SafetyReportRepository() }
    val homeScreenViewModel = remember { HomeScreenViewModel(safetyReportRepository) }
    val startScreenViewModel = remember { StartScreenViewModel(safetyReportRepository) }
    val gribViewModel = remember { GribViewModel() }

    // Track last location loaded to prevent redundant operations
    var lastLocationLoaded by remember { mutableStateOf<Int?>(null) }

    // Lifecycle observer to handle app resume events
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // When app resumes, ensure the persisted selected location is used
                val selectedLocationId = gribViewModel.getSelectedLocationId()
                Log.d(tag, "App resumed, persisted location ID: $selectedLocationId")

                if (selectedLocationId != null) {
                    val location = state.launchPoints.find { it.id == selectedLocationId }
                    if (location != null && location.id != lastLocationLoaded) {
                        Log.d(tag, "Restoring selected location: ${location.name}")
                        lastLocationLoaded = location.id

                        // Update HomeViewModel without fetching
                        homeScreenViewModel.selectLocation(location, false)

                        // Load data for this location
                        scope.launch {
                            homeScreenViewModel.getFourHourForecast(location.latitude, location.longitude)
                            gribViewModel.fetchGribData(location.latitude, location.longitude, location.name)
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    // Initialize data for the selected point if one exists
    LaunchedEffect(Unit) {
        if (state.launchPoints.isNotEmpty()) {
            val selectedPoint = state.launchPoints.find { it.selected }
            if (selectedPoint != null) {
                Log.d(tag, "Initial load with selected point: ${selectedPoint.name}")

                // Store selected location ID persistently
                gribViewModel.setSelectedLocationId(selectedPoint.id)
                lastLocationLoaded = selectedPoint.id

                // Update the HomeViewModel
                homeScreenViewModel.selectLocation(selectedPoint, false)

                // Fetch data
                homeScreenViewModel.getFourHourForecast(selectedPoint.latitude, selectedPoint.longitude)
                gribViewModel.fetchGribData(
                    selectedPoint.latitude,
                    selectedPoint.longitude,
                    selectedPoint.name
                )
            }
        }
    }

    // Monitor for changes in the selected location
    LaunchedEffect(state.launchPoints) {
        val selectedPoint = state.launchPoints.find { it.selected }

        if (selectedPoint != null) {
            // Only update if the selected location has actually changed
            if (selectedPoint.id != gribViewModel.getSelectedLocationId()) {
                Log.d(tag, "Selected location changed to: ${selectedPoint.name}")

                // Update persistent storage
                gribViewModel.setSelectedLocationId(selectedPoint.id)
                lastLocationLoaded = selectedPoint.id

                // Update ViewModel and fetch data
                homeScreenViewModel.selectLocation(selectedPoint, false)
                gribViewModel.fetchGribData(
                    selectedPoint.latitude,
                    selectedPoint.longitude,
                    selectedPoint.name
                )
            }
        }
    }

    // Track current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Handle navigation state restoration
    LaunchedEffect(currentRoute) {
        Log.d(tag, "Current route changed to $currentRoute")

        // When returning to Home screen, ensure location is consistent
        if (currentRoute == Screen.Home.route) {
            // Get the persistently stored location ID
            val persistedLocationId = gribViewModel.getSelectedLocationId()

            if (persistedLocationId != null) {
                // Find the location in our state
                val location = state.launchPoints.find { it.id == persistedLocationId }

                if (location != null) {
                    Log.d(tag, "Restoring location on Home return: ${location.name}")

                    // Check if the actual selected flag matches our persisted ID
                    if (!location.selected) {
                        Log.d(tag, "Selected flag doesn't match our persisted ID - fixing")
                        onEvent(LaunchPointEvent.UpdateLaunchPoint(location.copy(selected = true)))
                    }

                    // Always ensure the HomeViewModel has the correct location
                    homeScreenViewModel.selectLocation(location, false)

                    // Only reload data if we haven't just loaded this exact location
                    if (location.id != lastLocationLoaded) {
                        lastLocationLoaded = location.id
                        gribViewModel.fetchGribData(
                            location.latitude,
                            location.longitude,
                            location.name
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(route = Screen.Home.route) {
                    HomeScreen(
                        viewModel = homeScreenViewModel,
                        gribViewModel = gribViewModel,
                        navController = navController,
                        state = state,
                        onEvent = onEvent
                    )
                }
                composable(route = Screen.Start.route) {
                    StartScreen(viewModel = startScreenViewModel)
                }
                composable(route = Screen.Saved.route) {
                    SavedLocationScreen(
                        state = state,
                        onEvent = onEvent,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Start,
        Screen.Saved
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.Home -> Icon(Icons.Default.LocationOn, contentDescription = screen.label)
                        Screen.Start -> Icon(Icons.Default.RocketLaunch, contentDescription = screen.label)
                        Screen.Saved -> Icon(Icons.Default.Favorite, contentDescription = screen.label)
                    }
                },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
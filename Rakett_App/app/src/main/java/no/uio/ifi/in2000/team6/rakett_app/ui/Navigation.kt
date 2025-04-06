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
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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

    //Opprette repos og viewmodels
    val safetyReportRepository = SafetyReportRepository()
    val homeScreenViewModel = HomeScreenViewModel(safetyReportRepository)
    val startScreenViewModel = StartScreenViewModel(safetyReportRepository)
    val gribViewModel = GribViewModel()

    // Initialize data for the selected point if one exists
    LaunchedEffect(Unit) {
        if (state.launchPoints.isNotEmpty()) {
            val selectedPoint = state.launchPoints.find { it.selected }
            if (selectedPoint != null) {
                Log.d(tag, "Initial load with selected point: ${selectedPoint.name}")
                // Update ViewModel's selectedLocation
                homeScreenViewModel.selectLocation(selectedPoint, false)

                // Fetch weather data
                homeScreenViewModel.getFourHourForecast(selectedPoint.latitude, selectedPoint.longitude)

                // Fetch GRIB data
                gribViewModel.clearData()
                gribViewModel.fetchGribData(
                    selectedPoint.latitude,
                    selectedPoint.longitude,
                    selectedPoint.name
                )
            }
        }
    }

    // Listen for changes in saved locations
    LaunchedEffect(state.launchPoints) {
        val selectedPoint = state.launchPoints.find { it.selected }
        if (selectedPoint != null) {
            Log.d(tag, "LaunchedEffect: Location changed to ${selectedPoint.name}")
            // Update ViewModel's selectedLocation - don't fetch weather automatically
            homeScreenViewModel.selectLocation(selectedPoint, false)
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
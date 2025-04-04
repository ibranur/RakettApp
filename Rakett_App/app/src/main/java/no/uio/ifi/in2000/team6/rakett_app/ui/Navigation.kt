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

sealed class Screen(val route: String, val label: String) {
    data object Home : Screen("home", "Oppskytningssted")
    data object Start : Screen("start", "GO-VINDU")
    data object Saved : Screen("saved", "Lagret")
}


@Composable
fun Navigation(state: LaunchPointState,
               onEvent: (LaunchPointEvent) -> Unit) {
    val navController = rememberNavController()

    //Opprette repos og viewmodels
    val safetyReportRepository = SafetyReportRepository()
    val homeScreenViewModel = HomeScreenViewModel(safetyReportRepository)
    val startScreenViewModel = StartScreenViewModel(safetyReportRepository)
    val gribViewModel = GribViewModel()  // Legg til denne linjen

    //Kaller getFourHour.. funksjonen for punktet som er lagret.
    if (state.launchPoints.isNotEmpty()) {
        val selectedPoint = state.launchPoints.find { it.selected }
        val latitude = selectedPoint?.latitude
        val longitude = selectedPoint?.longitude
        if (latitude != null && longitude != null) {
            homeScreenViewModel.getFourHourForecast(latitude,longitude)
            gribViewModel.fetchGribData(latitude,longitude)  // Legg til denne linjen
            homeScreenViewModel.updateSelectedLocation(state)
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
                        gribViewModel = gribViewModel  // Legg til denne parameteren
                    )
                }
                composable(route = Screen.Start.route) {
                    StartScreen(viewModel = startScreenViewModel)
                }
                composable(route = Screen.Saved.route) {
                    //SavedLocationScreen()
                    SavedLocationScreen(
                        state = state,
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

// Resten av Navigation.kt forblir uendret

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
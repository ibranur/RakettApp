package no.uio.ifi.in2000.team6.rakett_app.ui

import StartScreenViewModel
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team6.rakett_app.data.Database.AppDatabase
import no.uio.ifi.in2000.team6.rakett_app.data.repository.LaunchPointRepository
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository
import no.uio.ifi.in2000.team6.rakett_app.ui.home.HomeScreen
import no.uio.ifi.in2000.team6.rakett_app.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.team6.rakett_app.ui.saved.SavedLocationScreen
import no.uio.ifi.in2000.team6.rakett_app.ui.saved.SavedLocationViewModel
import no.uio.ifi.in2000.team6.rakett_app.ui.start.StartScreen

sealed class Screen(val route: String, val label: String) {
    data object Home : Screen("home", "Oppskytningssted")
    data object Start : Screen("start", "GO-VINDU")
    data object Saved : Screen("saved", "Lagret")
}

/**
 * Hovednavigasjon for applikasjonen.
 * Setter opp navigasjonsgraf, ViewModels og databinding.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Oppretter repositories
    val safetyReportRepository = SafetyReportRepository()
    val launchPointDao = AppDatabase.getDatabase(context).launchPointDao()
    val launchPointRepository = LaunchPointRepository(launchPointDao)

    // Oppretter ViewModels
    val homeScreenViewModel = HomeScreenViewModel(safetyReportRepository)
    val startScreenViewModel = StartScreenViewModel(safetyReportRepository)
    val savedLocationViewModel = SavedLocationViewModel(launchPointRepository)

    // Henter tilstand fra SavedLocationViewModel
    val savedLocationState by savedLocationViewModel.state.collectAsState()

    // Sjekker valgt punkt for værmeldinger
    if (savedLocationState.launchPoints.isNotEmpty()) {
        // Oppdaterer værmeldinger for valgt oppskytningspunkt
        val selectedPoint = savedLocationState.launchPoints.find { it.selected }
        val latitude = selectedPoint?.latitude
        val longitude = selectedPoint?.longitude
        if (latitude != null && longitude != null) {
            homeScreenViewModel.getFourHourForecast(latitude, longitude)
            homeScreenViewModel.updateSelectedLocation(savedLocationState)
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
                    HomeScreen(viewModel = homeScreenViewModel)
                }
                composable(route = Screen.Start.route) {
                    StartScreen(viewModel = startScreenViewModel)
                }
                composable(route = Screen.Saved.route) {
                    SavedLocationScreen(
                        state = savedLocationState,
                        onEvent = savedLocationViewModel::onEvent
                    )
                }
            }
        }
    }
}

/**
 * Bunnnavigasjonsmeny for applikasjonen.
 */
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
                        Screen.Home -> Icon(
                            Icons.Default.LocationOn,
                            contentDescription = screen.label
                        )

                        Screen.Start -> Icon(
                            Icons.Default.RocketLaunch,
                            contentDescription = screen.label
                        )

                        Screen.Saved -> Icon(
                            Icons.Default.Favorite,
                            contentDescription = screen.label
                        )
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
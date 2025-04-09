package no.uio.ifi.in2000.team6.rakett_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import no.uio.ifi.in2000.team6.rakett_app.data.Database.LaunchPointDatabase
import no.uio.ifi.in2000.team6.rakett_app.data.repository.LaunchPointRepository
import no.uio.ifi.in2000.team6.rakett_app.ui.Navigation
import no.uio.ifi.in2000.team6.rakett_app.ui.saved.SavedLocationViewModel
import no.uio.ifi.in2000.team6.rakett_app.ui.theme.Rakett_AppTheme

/**
 * Hovedaktiviteten som initialiserer appen, setter opp databasen og
 * håndterer NavigationViewModel for navigasjon.
 */
class MainActivity : ComponentActivity() {

    // Initialiserer databasen ved første tilgang
    private val db by lazy {
        Room.databaseBuilder(applicationContext, LaunchPointDatabase::class.java, "launchPoints.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    // Oppretter ViewModel med factory for å sende inn repository
    @Suppress("UNCHECKED_CAST")
    private val viewModel by viewModels<SavedLocationViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    // Oppretter repository-instans og sender den til ViewModel
                    val repository = LaunchPointRepository(db.dao)
                    // Bruker suppress annotation for å unngå advarsel om typekasting
                    return SavedLocationViewModel(repository) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Rakett_AppTheme {
                // Henter state fra ViewModel for å sende til Navigation
                val state by viewModel.state.collectAsState()

                // Setter opp navigasjonen med state og event-handler
                Navigation(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}
package no.uio.ifi.in2000.team6.rakett_app

import android.os.Bundle
import android.util.Log
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

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    private val db by lazy {
        Room.databaseBuilder(applicationContext, LaunchPointDatabase::class.java, "launchPoints.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    private val viewModel by viewModels<SavedLocationViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    // Create repository instance and pass it to ViewModel
                    val repository = LaunchPointRepository(db.dao)
                    Log.d(TAG, "Creating SavedLocationViewModel with repository")
                    return SavedLocationViewModel(repository) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        enableEdgeToEdge()
        setContent {
            Rakett_AppTheme {
                val state by viewModel.state.collectAsState()
                Log.d(TAG, "Setting content with ${state.launchPoints.size} launch points")
                Navigation(state = state, onEvent = viewModel::onEvent)
            }
        }
    }
}
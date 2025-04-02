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
import no.uio.ifi.in2000.team6.rakett_app.ui.theme.Rakett_AppTheme


class MainActivity : ComponentActivity() {



    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            LaunchPointDatabase::class.java,
            "launchPoints.db")
            .fallbackToDestructiveMigration()
            .build()
    }


    private val viewModel by viewModels<LaunchPointViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LaunchPointViewModel(db.dao) as T
                }
            }
        }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Rakett_AppTheme {
                val state by viewModel.state.collectAsState()
                TestScreen(state = state, onEvent = viewModel::onEvent)
            }
            }
        }
    }

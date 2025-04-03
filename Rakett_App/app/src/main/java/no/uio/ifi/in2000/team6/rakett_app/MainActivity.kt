package no.uio.ifi.in2000.team6.rakett_app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.jakewharton.threetenabp.AndroidThreeTen
import no.uio.ifi.in2000.team6.rakett_app.ui.Navigation
import no.uio.ifi.in2000.team6.rakett_app.ui.theme.Rakett_AppTheme


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidThreeTen.init(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Rakett_AppTheme {
                val state by viewModel.state.collectAsState()
                Navigation()
            }
        }
    }
}

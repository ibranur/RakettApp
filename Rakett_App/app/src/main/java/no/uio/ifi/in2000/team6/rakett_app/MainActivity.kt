package no.uio.ifi.in2000.team6.rakett_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import no.uio.ifi.in2000.team6.rakett_app.ui.Navigation
import no.uio.ifi.in2000.team6.rakett_app.ui.theme.Rakett_AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Rakett_AppTheme {
                Navigation()
            }
        }
    }
}
package no.uio.ifi.in2000.team6.rakett_app.ui.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository


@Composable
fun HomeScreen(modifier: Modifier) {
    val viewModel = remember { HomeScreenViewModel(SafetyReportRepository()) }
    val temperature by viewModel.temperatureState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$temperature°C",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge
        )
    }
}
package no.uio.ifi.in2000.team6.rakett_app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository

@Composable
fun HomeScreen(modifier: Modifier) {
    var temperature by remember { mutableStateOf<Double?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val repository = SafetyReportRepository()
            val report = repository.getSafetyReport(59.9139, 10.7522)
            temperature = report.air_temperature
        } catch (e: Exception) {
            error = "Feil ved henting av temperatur: ${e.message}"
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            temperature != null -> Text(
                text = "${String.format("%.1f", temperature)}°C",
                textAlign = TextAlign.Center
            )
            error != null -> Text(
                text = error ?: "Ukjent feil",
                textAlign = TextAlign.Center
            )
            else -> Text(
                text = "Henter temperatur...",
                textAlign = TextAlign.Center
            )
        }
    }
}
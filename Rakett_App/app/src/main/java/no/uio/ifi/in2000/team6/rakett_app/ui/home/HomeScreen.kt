package no.uio.ifi.in2000.team6.rakett_app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val viewModel = remember { HomeScreenViewModel(SafetyReportRepository()) }
    val temperature by viewModel.temperatureState.collectAsState()
    val windSpeed by viewModel.windSpeedState.collectAsState()
    val windDirection by viewModel.windDirectionState.collectAsState()

    var latitude by remember { mutableStateOf("59.9139") }
    var longitude by remember { mutableStateOf("10.7522") }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input fields for latitude and longitude
        TextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Latitude") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Longitude") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Button to fetch weather data based on input coordinates
        Button(onClick = {
            val lat = latitude.toDoubleOrNull() ?: 59.9139
            val lon = longitude.toDoubleOrNull() ?: 10.7522
            viewModel.fetchWeatherData(lat, lon)
        }) {
            Text("Get Weather")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Display weather data
        Text(
            text = "$temperature°C",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Wind: $windSpeed m/s, Direction: $windDirection°",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

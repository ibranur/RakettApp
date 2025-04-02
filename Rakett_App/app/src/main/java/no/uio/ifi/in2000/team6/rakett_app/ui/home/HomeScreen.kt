package no.uio.ifi.in2000.team6.rakett_app.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.DayForecastCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel
) {

    val temperature by viewModel.temperatureState.collectAsState()
    val windSpeed by viewModel.windSpeedState.collectAsState()
    val windDirection by viewModel.windDirectionState.collectAsState()

    var latitude by remember { mutableStateOf("59.9139") }
    var longitude by remember { mutableStateOf("10.7522") }

    //Dropdown meny status
    var expanded by remember { mutableStateOf(false) }
    val savedCoordinates by viewModel.savedCoordinates.collectAsState()

    //weather forecast
    val fiveDayUIState by viewModel.fiveDayUIState.collectAsState()

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

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Get Weather button
            Button(onClick = {
                val lat = latitude.toDoubleOrNull() ?: 59.9139
                val lon = longitude.toDoubleOrNull() ?: 10.7522
                viewModel.getFiveDayForecast(lat,lon)
            }) {
                Text("Get Weather")
            }

            // Save button
            OutlinedButton(onClick = {
                val lat = latitude.toDoubleOrNull()
                val lon = longitude.toDoubleOrNull()
                if (lat != null && lon != null) {
                    viewModel.saveCoordinates(lat, lon)
                }
            }) {
                Text("Save")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Display weather data
        Column {
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(3.dp)
            )
            {
                items(fiveDayUIState.forecast.toList()) { (_, fiveday) ->
                    if (fiveday != null) {
                        DayForecastCard(
                            fiveDay = fiveday,
                            onClick = {}
                        )
                    }
                }
            }
        }

        //Dropdown menu
        Box {
            Button(onClick = { expanded = true }) {
                Text("Saved Coordinates")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false}
            ) {
                savedCoordinates.forEach { coordinate ->
                    DropdownMenuItem(
                        text = {Text("${coordinate.first}, ${coordinate.second}")},
                        onClick = {
                            latitude = coordinate.first.toString()
                            longitude = coordinate.second.toString()
                            expanded = false
                        }
                    )
                }
            }
        }
    }


}



@Composable
@Preview(showBackground = true)
@RequiresApi(Build.VERSION_CODES.O)
fun HomePreview() {
    HomeScreen()
}

package no.uio.ifi.in2000.team6.rakett_app.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team6.rakett_app.data.getSelectedPoint
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.AltitudeWeatherSection
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.ExpandableCard


// Her er endringene du trenger å gjøre i HomeScreen.kt:

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel,
    gribViewModel: GribViewModel  // Legg til denne parameteren
) {

    val temperature by viewModel.temperatureState.collectAsState()
    val windSpeed by viewModel.windSpeedState.collectAsState()
    val windDirection by viewModel.windDirectionState.collectAsState()

    var latitude by remember { mutableStateOf("59.9139") }
    var longitude by remember { mutableStateOf("10.7522") }

    //Dropdown meny status
    var expanded by remember { mutableStateOf(false) }
    val savedCoordinates by viewModel.savedCoordinates.collectAsState()

    val launchPointState by viewModel.launchPointState.collectAsState()

    //weather forecast
    val fourHourUIState by viewModel.fourHourUIState.collectAsState()

    // Hente Grib-data
    val gribMaps by gribViewModel.gribMaps.collectAsState()
    val windShearValues by gribViewModel.windShearValues.collectAsState()
    val isLoadingGrib by gribViewModel.isLoading.collectAsState()
    val errorMessage by gribViewModel.errorMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = getSelectedPoint(launchPointState.launchPoints),
            modifier = Modifier.padding(bottom = 8.dp).padding(horizontal = 40.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Været på bakkenivå de neste 4 timene",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 40.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // Display weather data
        Column {
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(3.dp)
            )
            {
                items(fourHourUIState.list) { fourHour ->
                    if (fourHour != null) {
                        ExpandableCard(
                            fourHour = fourHour,
                        )
                    }
                }
            }
        }

        //høydevind
        Spacer(modifier = Modifier.height(24.dp))

        AltitudeWeatherSection(
            modifier = Modifier.fillMaxWidth(),
            gribMaps = gribMaps,
            windShearValues = windShearValues,
            isLoading = isLoadingGrib,
            errorMessage = errorMessage,
            title = "Værdata i høyden nå"
        )

        //Dropdown menu - behold denne delen uendret
        //Box {
        //    Button(onClick = { expanded = true }) {
        //        Text("Saved Coordinates")
        //    }
        //    DropdownMenu(
        //        expanded = expanded,
        //        onDismissRequest = {expanded = false}
        //    ) {
        //        savedCoordinates.forEach { coordinate ->
        //            DropdownMenuItem(
        //                text = {Text("${coordinate.first}, ${coordinate.second}")},
        //                onClick = {
        //                    latitude = coordinate.first.toString()
        //                    longitude = coordinate.second.toString()
        //                    expanded = false
        //                }
        //            )
        //        }
        //    }
        //}
    }
}




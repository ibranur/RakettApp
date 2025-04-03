package no.uio.ifi.in2000.team6.rakett_app.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.ExpandableCard
import no.uio.ifi.in2000.team6.rakett_app.utils.getSelectedPoint

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

    val launchPointState by viewModel.launchPointState.collectAsState()


    //weather forecast
    val fourHourUIState by viewModel.fourHourUIState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = getSelectedPoint(launchPointState.launchPoints)
        )
        Text(
            text = "Været på bakkenivå de neste 4 timene",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display weather data
        Column {
            LazyColumn(
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

        //Dropdown menu
//        Box {
//            Button(onClick = { expanded = true }) {
//                Text("Saved Coordinates")
//            }
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = {expanded = false}
//            ) {
//                savedCoordinates.forEach { coordinate ->
//                    DropdownMenuItem(
//                        text = {Text("${coordinate.first}, ${coordinate.second}")},
//                        onClick = {
//                            latitude = coordinate.first.toString()
//                            longitude = coordinate.second.toString()
//                            expanded = false
//                        }
//                    )
//                }
//            }
//        }
    }


}




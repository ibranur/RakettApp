package no.uio.ifi.in2000.team6.rakett_app.ui.start

import StartScreenUiState
import StartScreenViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import no.uio.ifi.in2000.team6.rakett_app.data.repository.SafetyReportRepository
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.DateCard
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.DateWeatherInfo
import no.uio.ifi.in2000.team6.rakett_app.ui.cards.GoodTimeWindow
import no.uio.ifi.in2000.team6.rakett_app.ui.elements.TopBar
import org.threeten.bp.LocalDate


@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    viewModel: StartScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(
            title = "Vinduer for oppskytning",
            modifier = Modifier
        )

        // Content below the title
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DateRangePicker(
                fromDate = uiState.fromDate?.format(uiState.dateFormatter) ?: "Fra dato",
                toDate = uiState.toDate?.format(uiState.dateFormatter) ?: "Til dato",
                errorMessage = uiState.errorMessage,
                selectedDateRangeText = uiState.selectedDateRangeText,
                onFromDateSelected = viewModel::onFromDateSelected,
                onToDateSelected = viewModel::onToDateSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                    Text("Henter værdata...", modifier = Modifier.padding(8.dp))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                uiState.weatherData.isNotEmpty() -> {
                    LazyColumn {
                        items(uiState.weatherData) { dateInfo ->
                            DateCard(
                                weatherInfo = dateInfo,
                                onClick = { /* Handle click */ }
                            )
                        }
                    }
                }
                uiState.fromDate != null && uiState.toDate != null && uiState.errorMessage == null -> {
                    Text("Ingen værdata tilgjengelig for valgt periode")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePicker(
    fromDate: String,
    toDate: String,
    errorMessage: String?,
    selectedDateRangeText: String,
    onFromDateSelected: (Long?) -> Unit,
    onToDateSelected: (Long?) -> Unit
) {
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }

    val fromDatePickerState = rememberDatePickerState()
    val toDatePickerState = rememberDatePickerState()

    // From date picker dialog
    if (showFromDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showFromDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onFromDateSelected(fromDatePickerState.selectedDateMillis)
                    showFromDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFromDatePicker = false }) {
                    Text("Avbryt")
                }
            }
        ) {
            // Use Material3's DatePicker component (not our function)
            androidx.compose.material3.DatePicker(state = fromDatePickerState)
        }
    }

    // To date picker dialog
    if (showToDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showToDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onToDateSelected(toDatePickerState.selectedDateMillis)
                    showToDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showToDatePicker = false }) {
                    Text("Avbryt")
                }
            }
        ) {
            // Use Material3's DatePicker component (not our function)
            androidx.compose.material3.DatePicker(state = toDatePickerState)
        }
    }

    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {

        // Date picker buttons
        Row {
            Button(
                onClick = { showFromDatePicker = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text(fromDate)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { showToDatePicker = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text(toDate)
            }
        }

        // Error message
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Selected date range display
        if (selectedDateRangeText.isNotEmpty()) {
            Text(
                text = selectedDateRangeText,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun StartScreenEmptyPreview() {
    // Empty state - no dates selected
    val viewModel = remember {
        StartScreenViewModel(SafetyReportRepository()).apply {
            // Leave default empty state
        }
    }

    MaterialTheme {
        StartScreen(viewModel = viewModel)
    }
}
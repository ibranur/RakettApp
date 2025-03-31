package no.uio.ifi.in2000.team6.rakett_app.ui.start

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



import androidx.compose.ui.graphics.Color
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.Calendar

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    viewModel: StartScreenViewModel //brukes ikke enda.
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DatePicker()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    onDateRangeSelected: (from: LocalDate, to: LocalDate) -> Unit = { _, _ -> }
) {
    var fromDate by remember { mutableStateOf<LocalDate?>(null) }
    var toDate by remember { mutableStateOf<LocalDate?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    // Tilstander for datovelgere
    var showFromDatePicker by remember { mutableStateOf(false) }
    var showToDatePicker by remember { mutableStateOf(false) }

    // Datovelger-tilstander
    val fromDatePickerState = rememberDatePickerState()
    val toDatePickerState = rememberDatePickerState()

    // Kaller onDateRangeSelected automatisk når begge datoer er gyldige
    LaunchedEffect(fromDate, toDate, errorMessage) {
        if (fromDate != null && toDate != null && errorMessage == null) {
            onDateRangeSelected(fromDate!!, toDate!!)
        }
    }

    // Fra-dato velgerdialog
    if (showFromDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showFromDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Konverter millisekunder til LocalDate
                    fromDatePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = millis
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH) + 1
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        fromDate = LocalDate.of(year, month, day)

                        // Valider datoer
                        if (toDate != null && fromDate!!.isAfter(toDate)) {
                            errorMessage = "Fra-dato kan ikke være etter til-dato"
                        } else {
                            errorMessage = null
                        }
                    }
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
            DatePicker(state = fromDatePickerState)
        }
    }

    // Til-dato velgerdialog
    if (showToDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showToDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Konverter millisekunder til LocalDate
                    toDatePickerState.selectedDateMillis?.let { millis ->
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = millis
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH) + 1
                        val day = calendar.get(Calendar.DAY_OF_MONTH)
                        toDate = LocalDate.of(year, month, day)

                        // Valider datoer
                        if (fromDate != null && fromDate!!.isAfter(toDate)) {
                            errorMessage = "Til-dato kan ikke være før fra-dato"
                        } else {
                            errorMessage = null
                        }
                    }
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
            DatePicker(state = toDatePickerState)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Go-vindu"
        )
        // Datovelgerknapper
        Row {
            Button(
                onClick = { showFromDatePicker = true },
                modifier = Modifier.weight(1f),
                colors = ButtonColors(
                    contentColor = Color.Black,
                    containerColor = Color.LightGray,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.LightGray
                )
            ) {
                Text(fromDate?.format(dateFormatter) ?: "Fra dato")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { showToDatePicker = true },
                modifier = Modifier.weight(1f),
                colors = ButtonColors(
                    contentColor = Color.Black,
                    containerColor = Color.LightGray,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.LightGray
                )
            ) {
                Text(toDate?.format(dateFormatter) ?: "Til dato")
            }
        }

        // Feilmelding
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Visning av valgt periode (når begge datoer er gyldige)
        if (fromDate != null && toDate != null && errorMessage == null) {
            Text(
                text = "Valgt periode: ${fromDate!!.format(dateFormatter)} - ${toDate!!.format(dateFormatter)}",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
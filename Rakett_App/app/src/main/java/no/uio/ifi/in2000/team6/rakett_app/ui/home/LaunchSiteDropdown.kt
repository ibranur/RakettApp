package no.uio.ifi.in2000.team6.rakett_app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointState

/**
 * En dropdown-meny for å velge, administrere og legge til oppskytningssteder.
 */
@Composable
fun LaunchSiteDropdown(
    state: LaunchPointState,
    onEvent: (LaunchPointEvent) -> Unit,
    onShowAddDialog: () -> Unit,
    onShowEditDialog: (LaunchPoint) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedPoint = state.launchPoints.find { it.selected }
    val locationTextSize = 16.sp  // Slightly smaller text size for all items
    val itemHeight = 48.dp  // Fixed height for all dropdown items

    // This box will hold the card and ensure the dropdown is properly aligned
    Box(modifier = Modifier.fillMaxWidth()) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            // Hele kortet er klikkbart og åpner dropdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Tekst som viser valgt sted eller standardtekst
                Text(
                    text = selectedPoint?.name ?: "Velg oppskytningssted",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown-pil
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Åpne dropdown-meny",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }

        // Dropdown-menyen - aligned with the card above
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.918f) // Match dropdown width better with box over
        ) {
            // List opp eksisterende oppskytningssteder
            if (state.launchPoints.isEmpty()) {
                DropdownMenuItem(
                    modifier = Modifier.height(itemHeight),
                    text = {
                        Text(
                            "Ingen lagrede oppskytningssteder",
                            fontSize = locationTextSize
                        )
                    },
                    onClick = { }
                )
            } else {
                state.launchPoints.forEachIndexed { index, launchPoint ->
                    DropdownMenuItem(
                        modifier = Modifier.height(itemHeight),
                        text = {
                            Text(
                                text = launchPoint.name,
                                fontWeight = if (launchPoint.selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = locationTextSize
                            )
                        },
                        onClick = {
                            onEvent(LaunchPointEvent.UpdateLaunchPoint(launchPoint.copy(selected = true)))
                            expanded = false
                        },
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Rediger",
                                    modifier = Modifier
                                        .clickable {
                                            expanded = false
                                            onShowEditDialog(launchPoint)
                                        }
                                        .padding(horizontal = 8.dp)
                                )

                                // Using the same Icon consistently for all locations
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Slett",
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .clickable {
                                            onEvent(LaunchPointEvent.DeleteLaunchPoint(launchPoint))
                                        }
                                        .padding(horizontal = 8.dp)
                                )
                            }
                        }
                    )

                    // Add divider after each item except the last one
                    if (index < state.launchPoints.size - 1) {
                        HorizontalDivider()
                    }
                }
            }

            // Divider before Add button
            HorizontalDivider()

            // Legg til nytt oppskytningssted - moved to bottom
            DropdownMenuItem(
                modifier = Modifier.height(itemHeight),
                text = {
                    Text(
                        "Legg til lokasjon",
                        fontSize = locationTextSize  // Same size as location text
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Legg til"
                    )
                },
                onClick = {
                    expanded = false
                    onShowAddDialog()
                }
            )
        }
    }
}
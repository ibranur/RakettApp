package no.uio.ifi.in2000.team6.rakett_app.ui.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.ui.saved.SavedLocationDropdownMenu
import no.uio.ifi.in2000.team6.rakett_app.utils.CoordinateUtils


@Composable
fun CoordCard(
    launchPoint: LaunchPoint,
    onClick: (LaunchPointEvent) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 0.dp)
            .clickable { },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = launchPoint.name,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(text = "Lat. ${CoordinateUtils.formatLatitude(launchPoint.latitude)}")
                    Text(text = "Long. ${CoordinateUtils.formatLongitude(launchPoint.longitude)}")
                }
            }

            SavedLocationDropdownMenu(
                launchPoint,
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = onClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoordCardPreview() {
    // Sample LaunchPoint data
    val sampleLaunchPoint = LaunchPoint(
        latitude = 59.9139,
        longitude = 10.7522,
        name = "Oslo",
        selected = false,
        id = 1
    )

    // Preview the CoordCard with sample data
    CoordCard(
        launchPoint = sampleLaunchPoint,
        onClick = { /* No action needed for preview */ }
    )
}
package no.uio.ifi.in2000.team6.rakett_app.ui.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team6.rakett_app.DropdownMenuWithDetails
import no.uio.ifi.in2000.team6.rakett_app.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.LaunchPointEvent
import no.uio.ifi.in2000.team6.rakett_app.LaunchPointState

@Composable
fun CoordCard(
    launchPoint: LaunchPoint,
    onClick: (LaunchPointEvent) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            Column {
                Text(text = launchPoint.name, fontSize = 20.sp)
                Text(text = "latitude: ${launchPoint.latitude}")
                Text(text = "longitude: ${launchPoint.longitude}")
            }
//            IconButton(
//                modifier = Modifier.align(Alignment.TopEnd),
//                onClick = {
//                    onClick(LaunchPointEvent.DeleteLaunchPoint(launchPoint))
//                })
//            {
//                Icon(
//                    imageVector = Icons.Default.Delete,
//                    contentDescription = "Delete launch point"
//                )
//            }
            DropdownMenuWithDetails(
                launchPoint,
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = onClick,
            )
        }

    }
}
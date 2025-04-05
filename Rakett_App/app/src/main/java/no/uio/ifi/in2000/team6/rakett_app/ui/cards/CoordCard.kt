package no.uio.ifi.in2000.team6.rakett_app.ui.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team6.rakett_app.ui.saved.SavedLocationDropdownMenu
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPoint
import no.uio.ifi.in2000.team6.rakett_app.model.LocationSaving.LaunchPointEvent

@Composable
fun CoordCard(
    launchPoint: LaunchPoint,
    onClick: (LaunchPointEvent) -> Unit,
    navController: NavController? = null
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
            SavedLocationDropdownMenu(
                launchPoint = launchPoint,
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = onClick
            )
        }

    }
}
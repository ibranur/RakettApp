package no.uio.ifi.in2000.team6.rakett_app.ui.cards

import DetailedWeatherDisplay
import HourCard
import HourShortInfo
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHour

@Composable
fun ExpandableCard(fourHour: FourHour) {

    var expanded by remember { mutableStateOf (false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )
    Card(
        shape = RoundedCornerShape(8.dp),
        //elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable (
                onClick = { expanded = !expanded }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),

    ) {
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = "Drop-Down Arrow",
            modifier = Modifier
                .rotate(rotation)
                .align(alignment = Alignment.End)
                .padding(horizontal = 3.dp)
        )
        HourShortInfo(fourHour)

        if (expanded) {
            DetailedWeatherDisplay(fourHour)
        }

    }
}
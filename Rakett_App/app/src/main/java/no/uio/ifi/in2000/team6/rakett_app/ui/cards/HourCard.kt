package no.uio.ifi.in2000.team6.rakett_app.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team6.rakett_app.R
import no.uio.ifi.in2000.team6.rakett_app.ui.Rating.WeatherRatingIndicator

@Composable
fun HourCard(
    weatherInfo: HourWeatherInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp) // Reduced vertical padding
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp) // Reduced padding
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weatherInfo.hour,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold // Make hour stand out
                )
                WeatherRatingIndicator(weatherInfo.weatherScore)
            }
            Spacer(modifier = Modifier.height(8.dp)) // Adjusted spacing

            if (weatherInfo.details.isNotEmpty()) {
                ReasonDisplay(reasons = weatherInfo.details) // Pass list of Reasons
            } else {
                Text(text = "Details", fontSize = 12.sp) // Burde Erstatte details med data
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReasonDisplay(reasons: List<Reason>) { // Takes list of Reasons
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        reasons.forEach { reason ->
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                text = "${stringResource(reason.type.descriptionRes)}: ${reason.value}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun HourCardPreview() {
    val dummyWeatherInfo = HourWeatherInfo(
        hour = "14:00",
        weatherScore = 7.2,
        details = listOf(
            Reason(ReasonType.WIND, "5 m/s"),
            Reason(ReasonType.PRECIPITATION, "0 mm"),
            Reason(ReasonType.CLOUD_COVER, "30%"),
            Reason(ReasonType.HUMIDITY, "60%")
        )
    )
    HourCard(weatherInfo = dummyWeatherInfo) {}
}
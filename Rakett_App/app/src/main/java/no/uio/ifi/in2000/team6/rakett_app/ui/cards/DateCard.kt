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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team6.rakett_app.R
import no.uio.ifi.in2000.team6.rakett_app.data.repository.CalculationRepository
import no.uio.ifi.in2000.team6.rakett_app.ui.Rating.WeatherRatingIndicator

@Composable
fun DateCard(
    weatherInfo: DateWeatherInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = weatherInfo.date, style = MaterialTheme.typography.titleMedium)
                WeatherRatingIndicator(weatherInfo.weatherScore.toInt())
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Launch conditions summary
            LaunchConditionsSummary(weatherInfo)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Beste oppskytningstider:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (weatherInfo.goodTimeWindows.isNotEmpty()) {
                // Use Column for small lists
                if (weatherInfo.goodTimeWindows.size <= 3) {
                    Column {
                        weatherInfo.goodTimeWindows.forEach { window ->
                            TimeWindowItem(window)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                } else {
                    // Use LazyColumn with fixed height for larger lists
                    LazyColumn(modifier = Modifier.height(120.dp)) {
                        items(weatherInfo.goodTimeWindows) { window ->
                            TimeWindowItem(window)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.no_good_time_windows),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun LaunchConditionsSummary(weatherInfo: DateWeatherInfo) {
    // You can extract this data from weatherInfo or pass it separately
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "Dagssammendrag",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            SummaryRow("Vindforhold", if (weatherInfo.weatherScore > 5) "Gunstig" else "Ugunstig")
            SummaryRow("Score", "${weatherInfo.weatherScore}/10")
            SummaryRow("Antall vinduer", "${weatherInfo.goodTimeWindows.size}")
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TimeWindowItem(window: GoodTimeWindow) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = window.time,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View details",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Extract values for altitude calculation if available
            val airPressure = if (window.details.contains("Pressure:")) {
                window.details.substringAfter("Pressure:").substringBefore("hPa").trim().toIntOrNull()
            } else null

            val temperature = if (window.details.contains("Temp:")) {
                window.details.substringAfter("Temp:").substringBefore("°C").trim().toDoubleOrNull()
            } else null

            // Calculate altitude if we have the necessary values
            val altitude = if (airPressure != null && temperature != null) {
                CalculationRepository.toMeters(airPressure, temperature)
            } else null

            // Show altitude in UI if calculated
            if (altitude != null) {
                Text(
                    text = "Estimated altitude: ${String.format("%.1f", altitude)} meters",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Enhanced ReasonDisplay with multiple conditions
            EnhancedReasonDisplay(window.details)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EnhancedReasonDisplay(details: String) {
    // Parse the raw details string
    val reasons = mutableListOf<Reason>()

    // Extract wind information
    if (details.contains("Wind:")) {
        val windValue = details.substringAfter("Wind:").substringBefore(",").trim()
        reasons.add(Reason(ReasonType.WIND, windValue))
    }

    // Extract temperature
    if (details.contains("Temp:")) {
        val tempValue = details.substringAfter("Temp:").substringBefore(",").trim()
        reasons.add(Reason(ReasonType.TEMPERATURE, tempValue))
    }

    // Cloud coverage (would be extracted from actual data)
    if (details.contains("Cloud:")) {
        val cloudValue = details.substringAfter("Cloud:").substringBefore(",").trim()
        reasons.add(Reason(ReasonType.CLOUD_COVER, cloudValue))
    }

    // Add placeholder reasons if real data doesn't have them yet
    if (!details.contains("Humid:") && !details.contains("Limited")) {
        reasons.add(Reason(ReasonType.HUMIDITY, "45%"))
    }

    if (!details.contains("Precip:") && !details.contains("Limited")) {
        reasons.add(Reason(ReasonType.PRECIPITATION, "0 mm"))
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        reasons.forEach { reason ->
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(7.dp))
                    .background(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                text = "${getReasionLabel(reason.type)}: ${reason.value}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DateCardPreview() {
    val dummyWeatherInfo = DateWeatherInfo(
        date = "Monday 22. July",
        weatherScore = 8.5,
        goodTimeWindows = listOf(
            GoodTimeWindow("08:00-10:00", "Precip: 0.1 mm "),
            GoodTimeWindow("12:00-14:00", "Low wind: 0.1 m/s, Humidity: 30%"),
            GoodTimeWindow("16:00-18:00", "Clear skies, Precip: 0.2 mm, wind: 0.2m/s"),
        )
    )
    DateCard(weatherInfo = dummyWeatherInfo) {}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReasonDisplay(reason: String) {
    val reasonArray = reason.split(",").map { it.trim() }

    fun String.toReason(): Reason? {
        return when {
            contains("Precip") -> Reason(ReasonType.PRECIPITATION, substringAfter("Precip:").trim())
            contains("wind") -> Reason(ReasonType.WIND, substringAfter("wind:").trim())
            contains("Humidity") -> Reason(ReasonType.HUMIDITY, substringAfter("Humidity:").trim())
            contains("Clear skies") -> Reason(ReasonType.CLEAR_SKY, "")
            else -> null
        }
    }

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(2.dp), // Space between tags
        verticalArrangement = Arrangement.spacedBy(3.dp) // Space between rows of tags if they wrap
    ) {
        reasonArray.forEach { reasonString ->
            reasonString.toReason()?.let { reason ->
                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(7.dp)) // Rounded corners
                        .background(color = MaterialTheme.colorScheme.primary) // Primary color background
                        .padding(horizontal = 4.dp, vertical = 3.dp), // Padding inside the tag
                    text = "${stringResource(reason.type.descriptionRes)}: ${reason.value}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onPrimary // Text color on the primary background
                )
            }
        }
    }
}
@Composable
private fun getReasionLabel(type: ReasonType): String {
    return when (type) {
        ReasonType.WIND -> stringResource(R.string.reason_wind)
        ReasonType.PRECIPITATION -> stringResource(R.string.reason_precipitation)
        ReasonType.CLOUD_COVER -> stringResource(R.string.reason_cloud_cover)
        ReasonType.HUMIDITY -> stringResource(R.string.reason_humidity)
        ReasonType.THUNDER -> stringResource(R.string.reason_thunder)
        ReasonType.CLEAR_SKY -> stringResource(R.string.reason_clear_sky)
        ReasonType.TEMPERATURE -> "Temp"
        else -> ""
    }
}
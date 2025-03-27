package no.uio.ifi.in2000.team6.rakett_app.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team6.rakett_app.R
import no.uio.ifi.in2000.team6.rakett_app.ui.Rating.WeatherRatingIndicator

// 1. Use String Resources: Replace hardcoded strings with string resources for better
//    maintainability, localization, and consistency.
enum class ReasonType(val iconRes: Int, val descriptionRes: Int) {
    WIND(0, R.string.reason_wind),
    PRECIPITATION(0, R.string.reason_precipitation),
    CLOUD_COVER(0, R.string.reason_cloud_cover),
    HUMIDITY(0, R.string.reason_humidity),
    CLEAR_SKY(0, R.string.reason_clear_sky)
}

data class Reason(
    val type: ReasonType,
    val value: String
)

// 2. Use More Descriptive Names: Rename `GoodTimeWindow.reason` to `details` or
//    `description` for clarity.  The word "reason" is overloaded in this context.
data class GoodTimeWindow(
    val time: String,
    val details: String
)

data class DateWeatherInfo(
    val date: String,
    val weatherScore: Double,
    val goodTimeWindows: List<GoodTimeWindow>
)

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
                WeatherRatingIndicator(weatherInfo.weatherScore)
            }
            Spacer(modifier = Modifier.height(4.dp))

            if (weatherInfo.goodTimeWindows.isNotEmpty()) {
                // 3. Use `items` instead of `itemsIndexed`:  You don't use the index, so
                //    `items` is more concise and idiomatic.
                LazyColumn(modifier = Modifier.height(if (weatherInfo.goodTimeWindows.size > 3) 70.dp else Dp.Unspecified)) {
                    items(weatherInfo.goodTimeWindows) { window ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = window.time, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp)) // Add space between time and reasons
                            ReasonDisplay(reason = window.details)
                        }
                    }
                }
            } else {
                // 1. Use String Resources
                Text(text = stringResource(R.string.no_good_time_windows), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
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
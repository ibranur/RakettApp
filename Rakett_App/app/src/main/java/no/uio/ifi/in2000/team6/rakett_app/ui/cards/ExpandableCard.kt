package no.uio.ifi.in2000.team6.rakett_app.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.R
import no.uio.ifi.in2000.team6.rakett_app.data.ScoreHour
import no.uio.ifi.in2000.team6.rakett_app.data.getDrawableIdByName
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHour
import no.uio.ifi.in2000.team6.rakett_app.ui.Rating.WeatherRatingIndicator

@Composable
fun ExpandableCard(fourHour: FourHour) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header section (always visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hour
                Text(
                    text = fourHour.hour,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Weather icon
                val image = getDrawableIdByName(LocalContext.current, fourHour.symbol_code)
                Image(
                    painter = painterResource(image),
                    contentDescription = "weather symbol",
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Weather score
                    WeatherRatingIndicator(ScoreHour(fourHour))

                    // Dropdown arrow
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }

            // Expandable details section
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Divider(modifier = Modifier.padding(bottom = 8.dp))

                    // Weather details
                    WeatherRow(label = stringResource(R.string.wind_speed), value = "${fourHour.detailsInstant.wind_speed} m/s")
                    WeatherRow(label = stringResource(R.string.wind_gust), value = "${fourHour.detailsInstant.wind_speed_of_gust} m/s")
                    WeatherRow(label = stringResource(R.string.wind_direction), value = "${fourHour.detailsInstant.wind_from_direction}°")
                    WeatherRow(label = stringResource(R.string.precipitation), value = "${fourHour.detailsNext1Hour.precipitation_amount} mm")
                    WeatherRow(label = stringResource(R.string.cloud_cover), value = "${fourHour.detailsInstant.cloud_area_fraction}%")
                    WeatherRow(label = stringResource(R.string.humidity), value = "${fourHour.detailsInstant.relative_humidity.toInt()}%")
                    WeatherRow(label = stringResource(R.string.thunder_probability), value = "${fourHour.detailsNext1Hour.probability_of_thunder.toInt()}%")
                    WeatherRow(label = stringResource(R.string.air_pressure), value = "${fourHour.detailsInstant.air_pressure_at_sea_level} hPa")
                    WeatherRow(label = stringResource(R.string.air_temperature), value = "${fourHour.detailsInstant.air_temperature}°C")
                    WeatherRow(label = stringResource(R.string.dew_point), value = "${fourHour.detailsInstant.dew_point_temperature}°C")
                }
            }
        }
    }
}

@Composable
fun WeatherRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
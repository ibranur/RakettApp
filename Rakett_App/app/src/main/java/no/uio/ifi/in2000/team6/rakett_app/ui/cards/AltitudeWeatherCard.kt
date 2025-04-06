package no.uio.ifi.in2000.team6.rakett_app.ui.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team6.rakett_app.model.grib.GribMap
import kotlin.math.roundToInt

@Composable
fun AltitudeWeatherCard(
    altitude: Int,
    windSpeed: Int,
    windDirection: Int,
    windShear: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${altitude}m",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(80.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "☁️",
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Vind: ",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "$windSpeed m/s",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                windSpeed > 17.2 -> Color(0xFFF44336)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${windDirection}°",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Skjær: ",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "${windShear.roundToInt()} m/s/100m",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                windShear > 24.5 -> Color(0xFFF44336)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AltitudeWeatherList(
    modifier: Modifier = Modifier,
    gribMaps: List<GribMap>,
    windShearValues: List<Double>,
    isLoading: Boolean = false
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (gribMaps.isEmpty()) {
        Text(
            text = "Ingen tilgjengelig høydedata - kan kun vise data for Sør-Norge",
            color = Color.Red,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
        return
    }

    // Create pairs of grib data and wind shear values
    val adjustedWindShearValues = if (windShearValues.size < gribMaps.size - 1) {
        // Pad wind shear values if there are too few
        windShearValues + List(gribMaps.size - 1 - windShearValues.size) { 0.0 }
    } else if (gribMaps.size > 1) {
        // Trim wind shear values if there are too many
        windShearValues.take((gribMaps.size - 1).coerceAtLeast(0))
    } else {
        emptyList()
    }

    // The last grib map has no wind shear (since it's calculated between layers)
    val displayItems = gribMaps.mapIndexed { index, gribMap ->
        val windShear = if (index < adjustedWindShearValues.size) adjustedWindShearValues[index] else 0.0
        Pair(gribMap, windShear)
    }

    // Important: Set a fixed height for the LazyColumn to resolve the infinite height constraint error
    LazyColumn(
        modifier = modifier.height(300.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(displayItems) { pair ->
            val gribMap = pair.first
            val windShear = pair.second

            // Safe defaults in case of NaN or invalid values
            val safeAltitude = if (gribMap.altitude.isNaN()) 0 else gribMap.altitude.roundToInt()
            val safeWindSpeed = if (gribMap.wind_speed.isNaN()) 0 else gribMap.wind_speed.roundToInt()
            val safeWindDirection = if (gribMap.wind_direction.isNaN()) 0 else gribMap.wind_direction.roundToInt()
            val safeWindShear = if (windShear.isNaN()) 0.0 else windShear

            AltitudeWeatherCard(
                altitude = safeAltitude,
                windSpeed = safeWindSpeed,
                windDirection = safeWindDirection,
                windShear = safeWindShear
            )
        }
    }
}

@Composable
fun AltitudeWeatherSection(
    modifier: Modifier = Modifier,
    gribMaps: List<GribMap>,
    windShearValues: List<Double>,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    title: String = "Værdata i høyden nå"
) {
    Column(modifier = modifier) {
        // Only show title if provided
        if (title.isNotEmpty()) {
            Text(
                text = title,
                modifier = Modifier.padding(bottom = 4.dp),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // Only show error message if provided and no GRIB data is available
        if (!errorMessage.isNullOrEmpty() && gribMaps.isEmpty() && !isLoading) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        } else {
            // Pass along the GRIB data for display
            AltitudeWeatherList(
                modifier = Modifier.fillMaxWidth(),
                gribMaps = gribMaps,
                windShearValues = windShearValues,
                isLoading = isLoading
            )
        }
    }
}
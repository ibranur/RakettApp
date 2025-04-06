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
                    .padding(end = 8.dp),  // Lagt til padding på høyre side
                horizontalArrangement = Arrangement.End  // Justert til høyre
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
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Ingen tilgjengelig høydedata - kan kun vise data for Sør-Norge",
                color = Color.Red)
        }
        return
    }

    val safeWindShearValues = if (windShearValues.size == gribMaps.size - 1) {
        windShearValues + 0.0
    } else if (windShearValues.size < gribMaps.size) {
        windShearValues + List(gribMaps.size - windShearValues.size) { 0.0 }
    } else {
        windShearValues.take(gribMaps.size)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(gribMaps.zip(safeWindShearValues)) { (gribMap, windShear) ->
            AltitudeWeatherCard(
                altitude = gribMap.altitude.roundToInt(),
                windSpeed = gribMap.wind_speed.roundToInt(),
                windDirection = gribMap.wind_direction.roundToInt(),
                windShear = windShear
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
        if (title.isNotEmpty()) {
            Text(
                text = title,
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        AltitudeWeatherList(
            modifier = Modifier.fillMaxWidth(),
            gribMaps = gribMaps,
            windShearValues = windShearValues,
            isLoading = isLoading
        )
    }
}
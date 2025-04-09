package no.uio.ifi.in2000.team6.rakett_app.ui.cards

import DetailedWeatherDisplay
import HourShortInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast.DetailsInstant
import no.uio.ifi.in2000.team6.rakett_app.model.LocationForecast.DetailsNext1Hour
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHour

@Composable
fun ExpandableCard(fourHour: FourHour) {
    // Use rememberSaveable instead of remember to persist state through recompositions
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Animate the rotation of the dropdown arrow
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                expanded = !expanded
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Box {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Drop-Down Arrow",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .rotate(rotation)
                    .padding(horizontal = 3.dp)
            )
            HourShortInfo(fourHour)
        }

        // Use AnimatedVisibility for smoother transitions
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            DetailedWeatherDisplay(fourHour)
        }
    }
}

val fourHourTest = FourHour(
    detailsInstant = DetailsInstant(
        air_pressure_at_sea_level = 3.0,
        air_temperature = 3.0,
        cloud_area_fraction = 3.0,
        cloud_area_fraction_high = 3.0,
        cloud_area_fraction_low = 3.0,
        cloud_area_fraction_medium = 3.0,
        dew_point_temperature = 3.0,
        fog_area_fraction = 3.0,
        relative_humidity = 3.0,
        wind_from_direction = 3.0,
        wind_speed = 3.0,
        wind_speed_of_gust = 3.0
    ),
    detailsNext1Hour = DetailsNext1Hour(
        precipitation_amount = 3.0,
        precipitation_amount_max = 3.0,
        precipitation_amount_min = 3.0,
        probability_of_precipitation = 3.0,
        probability_of_thunder = 3.0
    ),
    hour = "25:00",
    symbol_code = "fair_day"
)

@Preview(showBackground = true)
@Composable
fun CardPrev() {
    ExpandableCard(fourHourTest)
}
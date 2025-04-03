package no.uio.ifi.in2000.team6.rakett_app.ui.cards

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team6.rakett_app.data.getDrawableIdByName
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FiveDay
import no.uio.ifi.in2000.team6.rakett_app.ui.Rating.WeatherRatingIndicator
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale



@Composable
fun DayForecastCard(
    fiveDay: FiveDay,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(9.dp)
                .height(90.dp)
                .fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            DayIconDegrees(fiveDay)
            WeatherInfoDay(fiveDay)

            Column (
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceAround
            ){
                    WeatherRatingIndicator(8)

                }
            }
        }
    }

@Composable
fun WeatherInfoDay(
    fiveDay: FiveDay,
) {
    Column (
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ){
        ValueAndUnit(value = "${fiveDay.wind_avg}", unit = "m/s")
        ValueAndUnit(value = "${fiveDay.precipitation_amount}", unit = "mm")
    }

}


@Composable
fun DayIconDegrees(
    fiveDay: FiveDay,
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(text = fiveDay.formattedTime, style = MaterialTheme.typography.titleMedium)
        val image = getDrawableIdByName(LocalContext.current, fiveDay.symbol_code)
        Row (

            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = "hei")
            Text(text = "${fiveDay.air_temperature_max}°", style = MaterialTheme.typography.titleMedium, fontSize = 29.sp)
        }

    }

}

@Composable
fun ValueAndUnit(value: String, unit: String) {
    Row (
        verticalAlignment = Alignment.Bottom
    ) {
        Text(text = value)
        Text(text = unit, fontSize = 5.sp)
    }
}

val five: FiveDay = FiveDay(
    time = ZonedDateTime.now(),
    air_temperature_max = 30,
    air_temperature_min = 26.3,
    precipitation_amount = 0.0,
    precipitation_amount_max = 0.0,
    precipitation_amount_min = 0.0,
    probability_of_precipitation = 0.0,
    symbol_code = "fair_day",
    formattedTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, d. MMMM", Locale.ENGLISH)),
    wind_avg = 3.2
)




@Preview(showBackground = true)
@Composable
fun CardPreview() {
    DayForecastCard(
        five,
        onClick = { },
    )
}


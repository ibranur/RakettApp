import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team6.rakett_app.R
import no.uio.ifi.in2000.team6.rakett_app.ui.Rating.WeatherRatingIndicator

// --- Data Classes ---

data class DetailedWeatherInfo(
    val windSpeed: String,
    val windGust: String,
    val windDirection: String,
    val precipitation: String,
    val cloudCover: String,
    val humidity: String,
    val thunderProbability: String,
    val airPressure: String,
    val airTemperature: String,
    // Add other relevant fields from the JSON here
)

data class HourWeatherInfo(
    val hour: String,
    val weatherScore: Double,
    val detailedInfo: DetailedWeatherInfo
)

// --- Composable ---

@Composable
fun HourCard(
    weatherInfo: HourWeatherInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                Text(
                    text = weatherInfo.hour,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                WeatherRatingIndicator(weatherInfo.weatherScore)
            }
            Spacer(modifier = Modifier.height(12.dp))

            DetailedWeatherDisplay(weatherInfo.detailedInfo)
        }
    }
}

@Composable
fun DetailedWeatherDisplay(info: DetailedWeatherInfo) {
    Column {
        // Use Rows to create a tabular layout
        WeatherRow(label = stringResource(R.string.wind_speed), value = info.windSpeed)
        WeatherRow(label = stringResource(R.string.wind_gust), value = info.windGust)
        WeatherRow(label = stringResource(R.string.wind_direction), value = info.windDirection)
        WeatherRow(label = stringResource(R.string.precipitation), value = info.precipitation)
        WeatherRow(label = stringResource(R.string.cloud_cover), value = info.cloudCover)
        WeatherRow(label = stringResource(R.string.humidity), value = info.humidity)
        WeatherRow(label = stringResource(R.string.thunder_probability), value = info.thunderProbability)
        WeatherRow(label = stringResource(R.string.air_pressure), value = info.airPressure)
        WeatherRow(label = stringResource(R.string.air_temperature), value = info.airTemperature)
    }
}

@Composable
fun WeatherRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

// --- Preview ---

@Preview(showBackground = true)
@Composable
fun HourCardPreview() {
    val dummyInfo = DetailedWeatherInfo(
        windSpeed = "5 m/s",
        windGust = "8 m/s",
        windDirection = "180° (South)",
        precipitation = "0 mm",
        cloudCover = "20%",
        humidity = "60%",
        thunderProbability = "5%",
        airPressure = "1015 hPa",
        airTemperature = "22°C"
    )
    val dummyWeatherInfo = HourWeatherInfo(
        hour = "14:00",
        weatherScore = 7.2,
        detailedInfo = dummyInfo
    )
    HourCard(weatherInfo = dummyWeatherInfo) {}
}
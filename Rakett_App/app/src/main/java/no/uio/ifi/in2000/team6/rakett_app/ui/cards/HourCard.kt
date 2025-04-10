import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team6.rakett_app.data.ScoreHour
import no.uio.ifi.in2000.team6.rakett_app.R
import no.uio.ifi.in2000.team6.rakett_app.data.getDrawableIdByName
import no.uio.ifi.in2000.team6.rakett_app.model.frontendForecast.FourHour
import no.uio.ifi.in2000.team6.rakett_app.ui.Rating.WeatherRatingIndicator

@Composable
fun HourShortInfo(
    fourHour: FourHour
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = fourHour.hour,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        val image = getDrawableIdByName(LocalContext.current, fourHour.symbol_code)
        Image(
            painter = painterResource(image),
            contentDescription = "weather symbol",
            modifier = Modifier
                .height(50.dp)
                .width(50.dp)
        )
        WeatherRatingIndicator(ScoreHour(fourHour))
    }
}

@Composable
fun DetailedWeatherDisplay(fourHour: FourHour) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Use Rows to create a tabular layout
        WeatherRow(label = stringResource(R.string.wind_speed), value = "${fourHour.detailsInstant.wind_speed} m/s")
        WeatherRow(label = stringResource(R.string.wind_gust), value = "${fourHour.detailsInstant.wind_speed_of_gust} m/s")
        WeatherRow(label = stringResource(R.string.wind_direction), value = "${fourHour.detailsInstant.wind_from_direction} °")
        WeatherRow(label = stringResource(R.string.precipitation), value = "${fourHour.detailsNext1Hour.precipitation_amount} mm")
        WeatherRow(label = stringResource(R.string.cloud_cover), value = "${fourHour.detailsInstant.cloud_area_fraction} %")
        WeatherRow(label = stringResource(R.string.humidity), value = "${fourHour.detailsInstant.relative_humidity.toInt()} %")
        WeatherRow(label = stringResource(R.string.thunder_probability), value = "${fourHour.detailsNext1Hour.probability_of_thunder.toInt()} %")
        WeatherRow(label = stringResource(R.string.air_pressure), value = "${fourHour.detailsInstant.air_pressure_at_sea_level} hPa")
        WeatherRow(label = stringResource(R.string.air_temperature), value = "${fourHour.detailsInstant.air_temperature} °C")
        WeatherRow(label = stringResource(R.string.dew_point), value = "${fourHour.detailsInstant.dew_point_temperature} °C")
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
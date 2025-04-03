package no.uio.ifi.in2000.team6.rakett_app.ui.Rating

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun WeatherRatingIndicator(score: Int) {
    val scoreColor = when {
        score >= 8-> Color.Green
        score >= 6 -> Color.Yellow
        else -> Color.Red
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(20.dp)) {
            drawCircle(color = scoreColor, radius = 10.dp.toPx())
        }
        Spacer(modifier = Modifier.size(5.dp))
        Text(
            text = "Score: $score/10",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
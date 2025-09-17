package com.example.wisp.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wisp.domain.model.WeatherHourly
import kotlin.math.max
import kotlin.math.min

@Composable
fun TemperatureChart(
    hourlyForecast: List<WeatherHourly>,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = hourlyForecast.isNotEmpty(), label = "chart_animation")
    
    val chartAlpha by transition.animateFloat(
        transitionSpec = { tween(800) },
        label = "chart_alpha"
    ) { visible ->
        if (visible) 1f else 0f
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "24-Hour Forecast",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (hourlyForecast.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(16.dp)
                ) {
                    // Simplified chart representation
                    Text(
                        text = "Temperature Chart\n${hourlyForecast.take(6).joinToString(", ") { "${it.tempC.toInt()}°C" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Time labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Now",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "12h",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "24h",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PrecipitationChart(
    hourlyForecast: List<WeatherHourly>,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = hourlyForecast.isNotEmpty(), label = "precipitation_chart_animation")
    
    val chartAlpha by transition.animateFloat(
        transitionSpec = { tween(800) },
        label = "precipitation_alpha"
    ) { visible ->
        if (visible) 1f else 0f
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Precipitation Forecast",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (hourlyForecast.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(16.dp)
                ) {
                    // Simplified precipitation chart representation
                    Text(
                        text = "Precipitation Chart\n${hourlyForecast.take(6).joinToString(", ") { "${it.precipMm.toInt()}mm" }}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Time labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Now",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "12h",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "24h",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

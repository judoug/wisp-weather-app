package com.example.wisp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.Air
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wisp.domain.model.WeatherBundle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeatherContent(
    weatherBundle: WeatherBundle,
    isOffline: Boolean = false,
    lastRefreshTime: Long? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "weather_animations")
    
    // Floating animation for weather icon
    val floatingAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )
    
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Location and offline indicator with animation
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = weatherBundle.place.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                AnimatedVisibility(
                    visible = isOffline,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.errorContainer,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Offline",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
        
        // Main weather card with animations
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(animationSpec = tween(800)) + fadeIn(animationSpec = tween(600)),
            exit = scaleOut() + fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Weather icon and temperature with floating animation
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Weather icon from OpenWeather API
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .alpha(0.9f)
                        ) {
                            AsyncImage(
                                model = "https://openweathermap.org/img/wn/01d@2x.png",
                                contentDescription = weatherBundle.now.condition,
                                modifier = Modifier
                                    .size(80.dp)
                                    .rotate(floatingAnimation * 0.1f),
                                contentScale = ContentScale.Fit
                            )
                        }
                        
                        Column {
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(
                                    animationSpec = tween(800, delayMillis = 200)
                                ) + fadeIn(animationSpec = tween(600, delayMillis = 200))
                            ) {
                                Text(
                                    text = "${weatherBundle.now.tempC.toInt()}°C",
                                    style = MaterialTheme.typography.displayLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInVertically(
                                    animationSpec = tween(800, delayMillis = 400)
                                ) + fadeIn(animationSpec = tween(600, delayMillis = 400))
                            ) {
                                Text(
                                    text = weatherBundle.now.condition,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Feels like temperature with animation
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInVertically(
                            animationSpec = tween(800, delayMillis = 600)
                        ) + fadeIn(animationSpec = tween(600, delayMillis = 600))
                    ) {
                        Text(
                            text = "Feels like ${weatherBundle.now.feelsLikeC.toInt()}°C",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Weather details with staggered animations
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Humidity card
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    animationSpec = tween(800, delayMillis = 800)
                ) + fadeIn(animationSpec = tween(600, delayMillis = 800)) + scaleIn(
                    animationSpec = tween(400, delayMillis = 800)
                ),
                exit = slideOutVertically() + fadeOut() + scaleOut()
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Water,
                            contentDescription = "Humidity",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${weatherBundle.now.humidity}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Humidity",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Wind speed card
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    animationSpec = tween(800, delayMillis = 1000)
                ) + fadeIn(animationSpec = tween(600, delayMillis = 1000)) + scaleIn(
                    animationSpec = tween(400, delayMillis = 1000)
                ),
                exit = slideOutVertically() + fadeOut() + scaleOut()
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Air,
                            contentDescription = "Wind",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${weatherBundle.now.windKph.toInt()} km/h",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Wind Speed",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        // Weather charts with animations
        if (weatherBundle.hourly.isNotEmpty()) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    animationSpec = tween(800, delayMillis = 1200)
                ) + fadeIn(animationSpec = tween(600, delayMillis = 1200)),
                exit = slideOutVertically() + fadeOut()
            ) {
                TemperatureChart(
                    hourlyForecast = weatherBundle.hourly,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    animationSpec = tween(800, delayMillis = 1400)
                ) + fadeIn(animationSpec = tween(600, delayMillis = 1400)),
                exit = slideOutVertically() + fadeOut()
            ) {
                PrecipitationChart(
                    hourlyForecast = weatherBundle.hourly,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Last refresh time with animation
        lastRefreshTime?.let { refreshTime ->
            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeString = formatter.format(Date(refreshTime))
            
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 1600)),
                exit = fadeOut()
            ) {
                Text(
                    text = "Last updated: $timeString",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

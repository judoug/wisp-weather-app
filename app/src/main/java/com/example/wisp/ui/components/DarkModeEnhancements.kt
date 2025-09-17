package com.example.wisp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DarkModeToggleCard(
    isDarkMode: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (isDarkMode) {
            Color(0xFF1A1A1A)
        } else {
            Color(0xFFF5F5F5)
        },
        animationSpec = tween(500),
        label = "background_color"
    )
    
    val animatedIconAlpha by animateFloatAsState(
        targetValue = if (isDarkMode) 1f else 0.7f,
        animationSpec = tween(300),
        label = "icon_alpha"
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .background(animatedBackgroundColor, RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Palette,
                    contentDescription = "Theme settings",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.alpha(animatedIconAlpha)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Theme",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkMode) Color.White else Color.Black
                )
            }
            
            // Dark Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = if (isDarkMode) Color(0xFF64B5F6) else Color(0xFFFFB74D),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Dark Mode",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (isDarkMode) Color.White else Color.Black
                        )
                        Text(
                            text = if (isDarkMode) "Enabled" else "Disabled",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkMode) Color(0xFFB0BEC5) else Color(0xFF757575)
                        )
                    }
                }
                
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = onToggle
                )
            }
        }
    }
}

@Composable
fun DynamicWeatherCard(
    weatherBundle: com.example.wisp.domain.model.WeatherBundle,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedCardColor by animateColorAsState(
        targetValue = if (isDarkMode) {
            Color(0xFF2D2D2D)
        } else {
            Color(0xFFFFFFFF)
        },
        animationSpec = tween(500),
        label = "card_color"
    )
    
    val animatedTextColor by animateColorAsState(
        targetValue = if (isDarkMode) {
            Color.White
        } else {
            Color.Black
        },
        animationSpec = tween(500),
        label = "text_color"
    )
    
    val animatedSecondaryTextColor by animateColorAsState(
        targetValue = if (isDarkMode) {
            Color(0xFFB0BEC5)
        } else {
            Color(0xFF757575)
        },
        animationSpec = tween(500),
        label = "secondary_text_color"
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkMode) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(containerColor = animatedCardColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Location
            Text(
                text = weatherBundle.place.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = animatedTextColor
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Temperature and condition
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${weatherBundle.now.tempC.toInt()}°C",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = animatedTextColor
                )
                
                Column {
                    Text(
                        text = weatherBundle.now.condition,
                        style = MaterialTheme.typography.titleMedium,
                        color = animatedSecondaryTextColor
                    )
                    Text(
                        text = "Feels like ${weatherBundle.now.feelsLikeC.toInt()}°C",
                        style = MaterialTheme.typography.bodyMedium,
                        color = animatedSecondaryTextColor
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weather details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetailItem(
                    label = "Humidity",
                    value = "${weatherBundle.now.humidity}%",
                    isDarkMode = isDarkMode
                )
                
                WeatherDetailItem(
                    label = "Wind",
                    value = "${weatherBundle.now.windKph.toInt()} km/h",
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

@Composable
private fun WeatherDetailItem(
    label: String,
    value: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedTextColor by animateColorAsState(
        targetValue = if (isDarkMode) Color.White else Color.Black,
        animationSpec = tween(300),
        label = "detail_text_color"
    )
    
    val animatedSecondaryColor by animateColorAsState(
        targetValue = if (isDarkMode) Color(0xFFB0BEC5) else Color(0xFF757575),
        animationSpec = tween(300),
        label = "detail_secondary_color"
    )
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = animatedTextColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = animatedSecondaryColor
        )
    }
}

@Composable
fun DynamicFloatingActionButton(
    onClick: () -> Unit,
    isDarkMode: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isDarkMode) {
            Color(0xFF1976D2)
        } else {
            Color(0xFF2196F3)
        },
        animationSpec = tween(500),
        label = "fab_color"
    )
    
    val animatedContentColor by animateColorAsState(
        targetValue = Color.White,
        animationSpec = tween(300),
        label = "fab_content_color"
    )
    
    androidx.compose.material3.FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = animatedColor,
        contentColor = animatedContentColor
    ) {
        // Icon content would go here
    }
}

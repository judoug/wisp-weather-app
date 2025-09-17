package com.example.wisp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AccessibilitySettingsCard(
    onHighContrastToggle: (Boolean) -> Unit,
    onLargeTextToggle: (Boolean) -> Unit,
    onScreenReaderToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var highContrastEnabled by remember { mutableStateOf(false) }
    var largeTextEnabled by remember { mutableStateOf(false) }
    var screenReaderEnabled by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    Icons.Default.Accessibility,
                    contentDescription = "Accessibility settings",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Accessibility",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // High Contrast Toggle
            AccessibilityToggleItem(
                title = "High Contrast",
                description = "Enhance color contrast for better visibility",
                enabled = highContrastEnabled,
                onToggle = { 
                    highContrastEnabled = !highContrastEnabled
                    onHighContrastToggle(highContrastEnabled)
                },
                icon = Icons.Default.Visibility
            )
            
            // Large Text Toggle
            AccessibilityToggleItem(
                title = "Large Text",
                description = "Increase text size for better readability",
                enabled = largeTextEnabled,
                onToggle = { 
                    largeTextEnabled = !largeTextEnabled
                    onLargeTextToggle(largeTextEnabled)
                },
                icon = Icons.Default.Accessibility
            )
            
            // Screen Reader Toggle
            AccessibilityToggleItem(
                title = "Screen Reader Support",
                description = "Enhanced content descriptions for screen readers",
                enabled = screenReaderEnabled,
                onToggle = { 
                    screenReaderEnabled = !screenReaderEnabled
                    onScreenReaderToggle(screenReaderEnabled)
                },
                icon = Icons.Default.VisibilityOff
            )
        }
    }
}

@Composable
private fun AccessibilityToggleItem(
    title: String,
    description: String,
    enabled: Boolean,
    onToggle: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$title: $description. Currently ${if (enabled) "enabled" else "disabled"}"
            }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Switch(
            checked = enabled,
            onCheckedChange = { onToggle() },
            modifier = Modifier.semantics {
                contentDescription = "Toggle $title"
            }
        )
    }
}

@Composable
fun HighContrastWeatherCard(
    weatherBundle: com.example.wisp.domain.model.WeatherBundle,
    isHighContrast: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = if (isHighContrast) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    
    val borderColor = if (isHighContrast) {
        MaterialTheme.colorScheme.outline
    } else {
        Color.Transparent
    }
    
    val textColor = if (isHighContrast) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isHighContrast) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHighContrast) 0.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = weatherBundle.place.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${weatherBundle.now.tempC.toInt()}°C",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                
                Text(
                    text = weatherBundle.now.condition,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun AccessibleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.semantics {
            this.contentDescription = contentDescription ?: text
        }
    ) {
        Text(text = text)
    }
}

@Composable
fun AccessibleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.semantics {
            this.contentDescription = contentDescription
        }
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

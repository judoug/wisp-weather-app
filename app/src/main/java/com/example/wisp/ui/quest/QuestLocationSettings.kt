package com.example.wisp.ui.quest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Quest-specific location settings component.
 * 
 * Features:
 * - IP geolocation toggle with privacy notice
 * - Manual location selection button
 * - Clear privacy messaging
 * - Quest-optimized touch targets
 */
@Composable
fun QuestLocationSettings(
    isIpLocationEnabled: Boolean,
    onIpLocationToggle: (Boolean) -> Unit,
    onManualLocationClick: () -> Unit,
    currentLocationName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .questCard(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(QuestUiUtils.QUEST_LARGE_SPACING),
            verticalArrangement = Arrangement.spacedBy(QuestUiUtils.QUEST_MEDIUM_SPACING)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(QuestUiUtils.QUEST_SMALL_SPACING)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Location Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Current location display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(QuestUiUtils.QUEST_SMALL_SPACING)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Current: $currentLocationName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // IP Location toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(QuestUiUtils.QUEST_SMALL_SPACING)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Use IP Location",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "Get approximate location from your IP address. This is less accurate but more private.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.width(QuestUiUtils.QUEST_MEDIUM_SPACING))
                
                Switch(
                    checked = isIpLocationEnabled,
                    onCheckedChange = onIpLocationToggle,
                    modifier = Modifier.questTouchTarget()
                )
            }
            
            // Manual location button
            OutlinedButton(
                onClick = onManualLocationClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .questButton()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.padding(end = QuestUiUtils.QUEST_SMALL_SPACING)
                )
                Text("Select Location Manually")
            }
            
            // Privacy notice
            Text(
                text = "Privacy: Your location is only used to provide weather information. " +
                      "IP location is approximate and less accurate than GPS. " +
                      "You can always select your location manually for better accuracy.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

package com.example.wisp.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wisp.domain.model.Place

@Composable
fun SwipeToDeleteContent(
    place: Place,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Delete background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.errorContainer,
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.error,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete location",
                    tint = MaterialTheme.colorScheme.onError
                )
            }
        }
        
        // Place card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${place.lat}, ${place.lon}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Note: Primary status would need to be passed as a parameter
                // or determined by comparing with a primary place ID
            }
        }
    }
}

@Composable
fun SwipeableLocationItem(
    place: Place,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        SwipeToDeleteContent(
            place = place,
            onDelete = onDelete
        )
    }
}

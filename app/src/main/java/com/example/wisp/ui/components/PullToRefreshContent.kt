package com.example.wisp.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PullToRefreshContent(
    isRefreshing: Boolean,
    pullOffset: Float,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = isRefreshing, label = "refresh_state")
    
    val rotation by transition.animateFloat(
        transitionSpec = { tween(200) },
        label = "rotation"
    ) { refreshing ->
        if (refreshing) 360f else 0f
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Pull to refresh",
                    modifier = Modifier
                        .size(32.dp)
                        .rotate(pullOffset * 0.5f),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Text(
                text = if (isRefreshing) "Refreshing..." else "Pull to refresh",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
    }
}

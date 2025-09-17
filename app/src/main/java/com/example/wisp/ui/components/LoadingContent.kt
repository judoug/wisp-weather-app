package com.example.wisp.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoadingContent(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    WeatherSkeletonScreen(modifier = modifier)
}

@Composable
fun WeatherSkeletonScreen(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "skeleton_animation")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeleton_alpha"
    )
    
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Location skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonBox(
                    modifier = Modifier.size(24.dp),
                    alpha = alpha
                )
                Spacer(modifier = Modifier.width(8.dp))
                SkeletonBox(
                    modifier = Modifier.width(120.dp).height(24.dp),
                    alpha = alpha
                )
            }
        }
        
        // Main weather card skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Weather icon skeleton
                    SkeletonBox(
                        modifier = Modifier.size(80.dp),
                        alpha = alpha,
                        shape = RoundedCornerShape(40.dp)
                    )
                    
                    Column {
                        SkeletonBox(
                            modifier = Modifier.width(100.dp).height(48.dp),
                            alpha = alpha
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SkeletonBox(
                            modifier = Modifier.width(80.dp).height(20.dp),
                            alpha = alpha
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                SkeletonBox(
                    modifier = Modifier.width(150.dp).height(20.dp),
                    alpha = alpha
                )
            }
        }
        
        // Weather details skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Humidity card skeleton
            Card(
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SkeletonBox(
                        modifier = Modifier.size(24.dp),
                        alpha = alpha
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SkeletonBox(
                        modifier = Modifier.width(40.dp).height(20.dp),
                        alpha = alpha
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    SkeletonBox(
                        modifier = Modifier.width(60.dp).height(14.dp),
                        alpha = alpha
                    )
                }
            }
            
            // Wind speed card skeleton
            Card(
                modifier = Modifier.weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SkeletonBox(
                        modifier = Modifier.size(24.dp),
                        alpha = alpha
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SkeletonBox(
                        modifier = Modifier.width(50.dp).height(20.dp),
                        alpha = alpha
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    SkeletonBox(
                        modifier = Modifier.width(70.dp).height(14.dp),
                        alpha = alpha
                    )
                }
            }
        }
    }
}

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    alpha: Float = 0.3f,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                shape
            )
    )
}

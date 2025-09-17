package com.example.wisp.ui.quest

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Quest-specific UI utilities for VR/AR panel applications.
 * 
 * Features:
 * - Large touch targets for controller/hand interaction
 * - Focus indicators for keyboard/controller navigation
 * - Quest-optimized spacing and sizing
 */

object QuestUiUtils {
    
    // Quest-optimized dimensions
    val MIN_TOUCH_TARGET_SIZE = 48.dp
    val QUEST_PANEL_MIN_WIDTH = 1200.dp
    val QUEST_PANEL_MAX_WIDTH = 1600.dp
    val QUEST_LARGE_SPACING = 24.dp
    val QUEST_MEDIUM_SPACING = 16.dp
    val QUEST_SMALL_SPACING = 8.dp
    
    // Focus indicator colors
    val FOCUS_INDICATOR_COLOR = Color(0xFF2196F3) // Blue
    val FOCUS_INDICATOR_WIDTH = 4.dp
}

/**
 * Modifier that adds Quest-optimized focus support with visual indicator.
 * 
 * @param showFocusIndicator Whether to show the focus indicator
 * @param focusColor Color of the focus indicator
 * @param focusWidth Width of the focus indicator
 */
fun Modifier.questFocusable(
    showFocusIndicator: Boolean = true,
    focusColor: Color = QuestUiUtils.FOCUS_INDICATOR_COLOR,
    focusWidth: Dp = QuestUiUtils.FOCUS_INDICATOR_WIDTH
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    this
        .focusable(interactionSource = interactionSource)
        .then(
            if (showFocusIndicator && isFocused) {
                Modifier.drawBehind {
                    drawFocusIndicator(
                        color = focusColor,
                        strokeWidth = focusWidth.toPx()
                    )
                }
            } else {
                Modifier
            }
        )
}

/**
 * Draws a focus indicator around the component.
 */
private fun DrawScope.drawFocusIndicator(
    color: Color,
    strokeWidth: Float
) {
    val strokeWidthPx = strokeWidth / 2f
    val rect = size.toRect()
    
    // Draw focus border
    drawLine(
        color = color,
        start = Offset(strokeWidthPx, strokeWidthPx),
        end = Offset(rect.width - strokeWidthPx, strokeWidthPx),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = color,
        start = Offset(rect.width - strokeWidthPx, strokeWidthPx),
        end = Offset(rect.width - strokeWidthPx, rect.height - strokeWidthPx),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = color,
        start = Offset(rect.width - strokeWidthPx, rect.height - strokeWidthPx),
        end = Offset(strokeWidthPx, rect.height - strokeWidthPx),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = color,
        start = Offset(strokeWidthPx, rect.height - strokeWidthPx),
        end = Offset(strokeWidthPx, strokeWidthPx),
        strokeWidth = strokeWidth
    )
}

/**
 * Modifier that ensures minimum touch target size for Quest controllers.
 */
fun Modifier.questTouchTarget(
    minSize: Dp = QuestUiUtils.MIN_TOUCH_TARGET_SIZE
): Modifier = this.then(
    Modifier.size(minSize)
)

/**
 * Composable that provides Quest-optimized spacing.
 */
@Composable
fun QuestSpacing(
    size: QuestSpacingSize = QuestSpacingSize.MEDIUM
) {
    val spacing = when (size) {
        QuestSpacingSize.SMALL -> QuestUiUtils.QUEST_SMALL_SPACING
        QuestSpacingSize.MEDIUM -> QuestUiUtils.QUEST_MEDIUM_SPACING
        QuestSpacingSize.LARGE -> QuestUiUtils.QUEST_LARGE_SPACING
    }
    
    androidx.compose.foundation.layout.Spacer(
        modifier = Modifier.size(spacing)
    )
}

enum class QuestSpacingSize {
    SMALL, MEDIUM, LARGE
}

/**
 * Quest-optimized button modifier with focus support.
 */
fun Modifier.questButton(): Modifier = this
    .questTouchTarget()
    .questFocusable()

/**
 * Quest-optimized card modifier with focus support.
 */
fun Modifier.questCard(): Modifier = this
    .questFocusable()

/**
 * Quest-optimized text field modifier with focus support.
 */
fun Modifier.questTextField(): Modifier = this
    .questFocusable()

package com.orbboard.boardoar.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OrbBall(
    title: String,
    color: Color,
    sizeDp: Dp,
    modifier: Modifier = Modifier,
    isCompleted: Boolean = false,
    showLabel: Boolean = true,
    isDragging: Boolean = false
) {
    val alpha = if (isCompleted) 0.45f else 1f
    val glow by animateFloatAsState(
        targetValue = if (isDragging) 1.4f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "glow_scale"
    )
    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.1f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "drag_scale"
    )

    val totalSize = sizeDp + 28.dp

    Box(
        modifier = modifier
            .size(totalSize)
            .graphicsLayer { scaleX = scale; scaleY = scale },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(totalSize)) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = sizeDp.toPx() / 2f

            // Outer glow rings
            for (i in 6 downTo 1) {
                drawCircle(
                    color = color.copy(alpha = 0.04f * (7 - i) * alpha * glow),
                    radius = radius + i * 5f,
                    center = Offset(cx, cy)
                )
            }

            // Main body - radial gradient for 3D sphere effect
            val lightColor = Color(
                red = (color.red + (1f - color.red) * 0.35f).coerceAtMost(1f),
                green = (color.green + (1f - color.green) * 0.35f).coerceAtMost(1f),
                blue = (color.blue + (1f - color.blue) * 0.35f).coerceAtMost(1f),
                alpha = alpha
            )
            val darkColor = Color(
                red = color.red * 0.45f,
                green = color.green * 0.45f,
                blue = color.blue * 0.45f,
                alpha = alpha
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(lightColor, color.copy(alpha = alpha), darkColor),
                    center = Offset(cx - radius * 0.25f, cy - radius * 0.3f),
                    radius = radius * 2f
                ),
                radius = radius,
                center = Offset(cx, cy)
            )

            // Primary glossy highlight (top-left)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.75f * alpha),
                        Color.White.copy(alpha = 0.15f * alpha),
                        Color.Transparent
                    ),
                    center = Offset(cx - radius * 0.28f, cy - radius * 0.33f),
                    radius = radius * 0.44f
                ),
                radius = radius * 0.44f,
                center = Offset(cx - radius * 0.28f, cy - radius * 0.33f)
            )

            // Secondary small highlight (bottom-right)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.18f * alpha),
                        Color.Transparent
                    ),
                    center = Offset(cx + radius * 0.3f, cy + radius * 0.3f),
                    radius = radius * 0.22f
                ),
                radius = radius * 0.22f,
                center = Offset(cx + radius * 0.3f, cy + radius * 0.3f)
            )
        }

        if (showLabel) {
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completed",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(sizeDp * 0.35f)
                )
            } else {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = (sizeDp.value / 7f).coerceIn(8f, 13f).sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = (sizeDp.value / 6f).coerceIn(10f, 16f).sp,
                    modifier = Modifier
                        .widthIn(max = sizeDp * 0.85f)
                        .padding(horizontal = 4.dp)
                )
            }
        }
    }
}

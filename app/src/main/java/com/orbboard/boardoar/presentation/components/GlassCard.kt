package com.orbboard.boardoar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.orbboard.boardoar.ui.theme.orb

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    backgroundColor: Color = Color.Unspecified,
    borderColor: Color = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit
) {
    val palette = MaterialTheme.orb
    val bg = if (backgroundColor == Color.Unspecified) palette.glassWhite else backgroundColor
    val border = if (borderColor == Color.Unspecified) palette.glassBorder else borderColor

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(cornerRadius)),
        content = content
    )
}

@Composable
fun GlassButton(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 50.dp,
    accentColor: Color = Color.Unspecified,
    content: @Composable BoxScope.() -> Unit
) {
    val palette = MaterialTheme.orb
    val accent = if (accentColor == Color.Unspecified) palette.neonBlue else accentColor

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(accent.copy(alpha = 0.12f))
            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(cornerRadius))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        content = content
    )
}

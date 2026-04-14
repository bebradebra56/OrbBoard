package com.orbboard.boardoar.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private fun buildColorScheme(p: OrbColorPalette) = darkColorScheme(
    primary = p.neonBlue,
    onPrimary = p.backgroundDark,
    primaryContainer = p.surfaceDark,
    onPrimaryContainer = p.textPrimary,
    secondary = p.neonPurple,
    onSecondary = p.textPrimary,
    secondaryContainer = p.surfaceLight,
    onSecondaryContainer = p.textPrimary,
    tertiary = p.neonPink,
    onTertiary = p.textPrimary,
    tertiaryContainer = p.surfaceDark,
    onTertiaryContainer = p.textPrimary,
    background = p.backgroundDark,
    onBackground = p.textPrimary,
    surface = p.backgroundMedium,
    onSurface = p.textPrimary,
    surfaceVariant = p.surfaceDark,
    onSurfaceVariant = p.textSecondary,
    outline = p.textTertiary,
    outlineVariant = p.glassBorder,
    error = Color(0xFFFF5252),
    onError = p.textPrimary
)

@Composable
fun OrbBoardTheme(
    colorMode: String = "Neon",
    content: @Composable () -> Unit
) {
    val palette = when (colorMode) {
        "Soft" -> SoftPalette
        "Minimal" -> MinimalPalette
        else -> NeonPalette
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    CompositionLocalProvider(LocalOrbColors provides palette) {
        MaterialTheme(
            colorScheme = buildColorScheme(palette),
            typography = OrbTypography,
            content = content
        )
    }
}

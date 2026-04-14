package com.orbboard.boardoar.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// All color constants are theme-aware: they resolve to the active OrbColorPalette
// provided via LocalOrbColors, so switching themes recomposes the entire UI automatically.

val BackgroundDark: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.backgroundDark

val BackgroundDark2: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.backgroundDark2

val BackgroundMedium: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.backgroundMedium

val SurfaceDark: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.surfaceDark

val SurfaceLight: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.surfaceLight

val NeonBlue: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.neonBlue

val NeonPink: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.neonPink

val NeonYellow: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.neonYellow

val NeonPurple: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.neonPurple

val NeonLime: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.neonLime

val TextPrimary: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.textPrimary

val TextSecondary: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.textSecondary

val TextTertiary: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.textTertiary

val GlassWhite: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.glassWhite

val GlassBorder: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.glassBorder

val CategoryWork: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.neonPurple

val CategoryPersonal: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.neonPink

val CategoryIdeas: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.neonYellow

val CategoryShopping: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.neonLime

val CategoryGoals: Color
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.neonBlue

val OrbColors: List<String>
    @Composable @ReadOnlyComposable
    get() = LocalOrbColors.current.orbColors

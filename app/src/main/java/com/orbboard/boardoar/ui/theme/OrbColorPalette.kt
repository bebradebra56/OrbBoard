package com.orbboard.boardoar.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class OrbColorPalette(
    val backgroundDark: Color,
    val backgroundDark2: Color,
    val backgroundMedium: Color,
    val surfaceDark: Color,
    val surfaceLight: Color,
    val neonBlue: Color,
    val neonPink: Color,
    val neonYellow: Color,
    val neonPurple: Color,
    val neonLime: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val glassWhite: Color,
    val glassBorder: Color,
    val gridLine: Color,
    val gridDot: Color,
    val orbColors: List<String>
)

val NeonPalette = OrbColorPalette(
    backgroundDark = Color(0xFF1B1235),
    backgroundDark2 = Color(0xFF0D0825),
    backgroundMedium = Color(0xFF231644),
    surfaceDark = Color(0xFF2D1E55),
    surfaceLight = Color(0xFF3D2A70),
    neonBlue = Color(0xFF3ED2FF),
    neonPink = Color(0xFFFF4FCB),
    neonYellow = Color(0xFFFFD84A),
    neonPurple = Color(0xFF7A5CFF),
    neonLime = Color(0xFF5DFF8F),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFB0A8D0),
    textTertiary = Color(0xFF7A6FAA),
    glassWhite = Color(0x1AFFFFFF),
    glassBorder = Color(0x33FFFFFF),
    gridLine = Color(0xFF2A1B50),
    gridDot = Color(0xFF3D2A6A),
    orbColors = listOf(
        "#7A5CFF", "#FF4FCB", "#FFD84A", "#5DFF8F", "#3ED2FF",
        "#FF6B6B", "#FF9F43", "#A29BFE", "#FD79A8", "#00CEC9"
    )
)

val SoftPalette = OrbColorPalette(
    backgroundDark = Color(0xFF151B2E),
    backgroundDark2 = Color(0xFF0B0F1C),
    backgroundMedium = Color(0xFF1E2542),
    surfaceDark = Color(0xFF252E52),
    surfaceLight = Color(0xFF2E3A65),
    neonBlue = Color(0xFF7EC8E3),
    neonPink = Color(0xFFE8A4C8),
    neonYellow = Color(0xFFF5D78E),
    neonPurple = Color(0xFFAD9BF0),
    neonLime = Color(0xFF8BE8A8),
    textPrimary = Color(0xFFEEF0FF),
    textSecondary = Color(0xFF9AAABF),
    textTertiary = Color(0xFF566A88),
    glassWhite = Color(0x14FFFFFF),
    glassBorder = Color(0x22FFFFFF),
    gridLine = Color(0xFF1C2440),
    gridDot = Color(0xFF283255),
    orbColors = listOf(
        "#AD9BF0", "#E8A4C8", "#F5D78E", "#8BE8A8", "#7EC8E3",
        "#F0A4A4", "#F5C87E", "#B8B0F8", "#F5A4C0", "#7EC8D8"
    )
)

val MinimalPalette = OrbColorPalette(
    backgroundDark = Color(0xFF0F0F0F),
    backgroundDark2 = Color(0xFF050505),
    backgroundMedium = Color(0xFF1A1A1A),
    surfaceDark = Color(0xFF242424),
    surfaceLight = Color(0xFF2E2E2E),
    neonBlue = Color(0xFFE0E0E0),
    neonPink = Color(0xFFBDBDBD),
    neonYellow = Color(0xFFFFFFFF),
    neonPurple = Color(0xFFCCCCCC),
    neonLime = Color(0xFF9E9E9E),
    textPrimary = Color(0xFFF5F5F5),
    textSecondary = Color(0xFF9E9E9E),
    textTertiary = Color(0xFF616161),
    glassWhite = Color(0x0DFFFFFF),
    glassBorder = Color(0x1AFFFFFF),
    gridLine = Color(0xFF1E1E1E),
    gridDot = Color(0xFF2C2C2C),
    orbColors = listOf(
        "#E0E0E0", "#BDBDBD", "#9E9E9E", "#757575", "#F5F5F5",
        "#EEEEEE", "#CFCFCF", "#B0B0B0", "#D4D4D4", "#888888"
    )
)

val LocalOrbColors = compositionLocalOf { NeonPalette }

val MaterialTheme.orb: OrbColorPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalOrbColors.current

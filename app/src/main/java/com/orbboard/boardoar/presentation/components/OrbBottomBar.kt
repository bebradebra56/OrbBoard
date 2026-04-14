package com.orbboard.boardoar.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.orbboard.boardoar.presentation.navigation.Screen
import com.orbboard.boardoar.ui.theme.*

private data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun OrbBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem(Screen.Board.route, Icons.Default.GridView, "Board"),
        BottomNavItem(Screen.Categories.route, Icons.Default.Category, "Categories"),
        BottomNavItem(Screen.Activity.route, Icons.Default.History, "Activity"),
        BottomNavItem(Screen.Settings.route, Icons.Default.Settings, "Settings")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundMedium.copy(alpha = 0.97f))
    ) {
        HorizontalDivider(
            color = GlassBorder,
            thickness = 1.dp
        )
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                val iconColor by animateColorAsState(
                    targetValue = if (isSelected) NeonBlue else TextSecondary,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "icon_color_${item.route}"
                )
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(item.route) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = iconColor
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonBlue,
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = NeonBlue,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = NeonBlue.copy(alpha = 0.14f)
                    )
                )
            }
        }
    }
}

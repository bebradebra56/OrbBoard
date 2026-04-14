package com.orbboard.boardoar.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orbboard.boardoar.presentation.components.GlassCard
import com.orbboard.boardoar.ui.theme.*
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val shareLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    LaunchedEffect(uiState.showExportSuccess) {
        if (uiState.showExportSuccess) {
            val uri = viewModel.consumeBackupUri()
            if (uri != null) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/octet-stream"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                shareLauncher.launch(Intent.createChooser(intent, "Export Orb Board Backup"))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, BackgroundDark2)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + 16.dp
            )
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Personalize your experience",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            item {
                SettingsSectionHeader("Appearance")
            }

            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    cornerRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Theme",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Changes colors across the entire app",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ThemePreviewCard(
                                label = "Neon",
                                palette = NeonPalette,
                                isSelected = uiState.colorMode == "Neon",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setColorMode("Neon") }
                            )
                            ThemePreviewCard(
                                label = "Soft",
                                palette = SoftPalette,
                                isSelected = uiState.colorMode == "Soft",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setColorMode("Soft") }
                            )
                            ThemePreviewCard(
                                label = "Minimal",
                                palette = MinimalPalette,
                                isSelected = uiState.colorMode == "Minimal",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setColorMode("Minimal") }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    cornerRadius = 18.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Board Style",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Background pattern on the board",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            BoardStylePreviewCard(
                                label = "Grid",
                                style = "Grid",
                                isSelected = uiState.boardStyle == "Grid",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setBoardStyle("Grid") }
                            )
                            BoardStylePreviewCard(
                                label = "Dots",
                                style = "Dots",
                                isSelected = uiState.boardStyle == "Dots",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setBoardStyle("Dots") }
                            )
                            BoardStylePreviewCard(
                                label = "None",
                                style = "None",
                                isSelected = uiState.boardStyle == "None",
                                modifier = Modifier.weight(1f),
                                onClick = { viewModel.setBoardStyle("None") }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(16.dp))
                SettingsSectionHeader("Data")
            }

            item {
                SettingsActionRow(
                    icon = Icons.Default.CloudUpload,
                    iconColor = NeonBlue,
                    title = "Backup",
                    subtitle = "Export database to share",
                    isLoading = uiState.isExporting,
                    onClick = viewModel::exportBackup
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
                SettingsSectionHeader("About")
            }

            item {
                SettingsActionRow(
                    icon = Icons.Default.Policy,
                    iconColor = NeonBlue,
                    title = "Privacy Policy",
                    subtitle = "Tap to read",
                    isLoading = uiState.isExporting,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://orbboard.com/privacy-policy.html"))
                        context.startActivity(intent)
                    }
                )
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    cornerRadius = 18.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "✦",
                                fontSize = androidx.compose.ui.unit.TextUnit(
                                    20f,
                                    androidx.compose.ui.unit.TextUnitType.Sp
                                ),
                                color = NeonPurple
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Orb Board",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Version 1.0.0",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Text(
                                "Visual task planner",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (uiState.showExportError) {
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Backup Failed", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Could not export the database. Please try again.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::dismissError) {
                    Text("OK", color = NeonBlue)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = TextTertiary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun ThemePreviewCard(
    label: String,
    palette: OrbColorPalette,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) palette.neonBlue else GlassBorder
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        // Mini theme preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    Brush.verticalGradient(listOf(palette.backgroundDark, palette.backgroundDark2))
                )
        ) {
            // Three colored orb dots
            Row(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(palette.neonPurple, palette.neonBlue, palette.neonPink).forEach { c ->
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(c.copy(alpha = 0.9f))
                    )
                }
            }
        }
        // Label at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    if (isSelected) palette.neonBlue.copy(alpha = 0.18f)
                    else SurfaceDark.copy(alpha = 0.9f)
                )
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) palette.neonBlue else TextSecondary,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun BoardStylePreviewCard(
    label: String,
    style: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val accentColor = if (isSelected) NeonBlue else GlassBorder
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val gridLine = LocalOrbColors.current.gridLine
    val gridDot = LocalOrbColors.current.gridDot

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(borderWidth, accentColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(BackgroundDark)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val spacing = 16.dp.toPx()
                when (style) {
                    "Grid" -> {
                        var x = 0f
                        while (x <= size.width) {
                            drawLine(
                                gridLine,
                                Offset(x, 0f),
                                Offset(x, size.height),
                                strokeWidth = 1f
                            )
                            x += spacing
                        }
                        var y = 0f
                        while (y <= size.height) {
                            drawLine(
                                gridLine,
                                Offset(0f, y),
                                Offset(size.width, y),
                                strokeWidth = 1f
                            )
                            y += spacing
                        }
                        x = 0f
                        while (x <= size.width) {
                            var y2 = 0f
                            while (y2 <= size.height) {
                                drawCircle(gridDot, 1.5f, Offset(x, y2))
                                y2 += spacing
                            }
                            x += spacing
                        }
                    }

                    "Dots" -> {
                        var x = 0f
                        while (x <= size.width) {
                            var y = 0f
                            while (y <= size.height) {
                                drawCircle(gridDot.copy(alpha = 0.8f), 2f, Offset(x, y))
                                y += spacing
                            }
                            x += spacing
                        }
                    }
                    // "None" → blank preview
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    if (isSelected) NeonBlue.copy(alpha = 0.18f)
                    else SurfaceDark.copy(alpha = 0.9f)
                )
                .padding(vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) NeonBlue else TextSecondary,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        cornerRadius = 18.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Switch(
                checked = checked,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = NeonBlue,
                    uncheckedThumbColor = TextTertiary,
                    uncheckedTrackColor = SurfaceLight
                )
            )
        }
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(enabled = !isLoading, onClick = onClick),
        cornerRadius = 18.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = iconColor,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Default.ChevronRight,
                    null,
                    tint = TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

package com.orbboard.boardoar.presentation.focus

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.presentation.components.GlassCard
import com.orbboard.boardoar.presentation.components.OrbBall
import com.orbboard.boardoar.ui.theme.*
import com.orbboard.boardoar.util.formatTime
import com.orbboard.boardoar.util.parseColor
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FocusScreen(
    initialOrbId: Long,
    paddingValues: PaddingValues,
    onBack: () -> Unit,
    viewModel: FocusViewModel = koinViewModel(parameters = { parametersOf(initialOrbId) })
) {
    val uiState by viewModel.uiState.collectAsState()

    val timerPulse by rememberInfiniteTransition(label = "timer_pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (uiState.isRunning) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, BackgroundDark2)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = TextSecondary)
                }
                Text(
                    "Focus Mode",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Selected orb with timer
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .scale(if (uiState.isRunning) timerPulse else 1f)
            ) {
                uiState.selectedOrb?.let { orb ->
                    OrbBall(
                        title = "",
                        color = parseColor(orb.colorHex),
                        sizeDp = 180.dp,
                        showLabel = false
                    )
                } ?: run {
                    OrbBall(
                        title = "",
                        color = NeonPurple,
                        sizeDp = 180.dp,
                        showLabel = false
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        formatTime(uiState.remainingSeconds),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp
                    )
                    Text(
                        uiState.selectedOrb?.title ?: "No orb selected",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }

            // Completion celebration
            AnimatedVisibility(visible = uiState.isCompleted) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text("🎉 Focus session complete!", color = NeonLime, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = viewModel::markOrbComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                        shape = RoundedCornerShape(50)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = BackgroundDark, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Mark Orb Complete", color = BackgroundDark, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Duration selector
            if (!uiState.isRunning && !uiState.isPaused) {
                Text("Duration", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(15, 25, 45).forEach { minutes ->
                        val isSelected = uiState.durationMinutes == minutes
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (isSelected) NeonPurple.copy(alpha = 0.2f) else SurfaceDark)
                                .border(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) NeonPurple else GlassBorder,
                                    RoundedCornerShape(50)
                                )
                                .clickable { viewModel.setDuration(minutes) }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                "$minutes min",
                                color = if (isSelected) NeonPurple else TextSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Timer controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.isRunning || uiState.isPaused) {
                    // Stop button
                    IconButton(
                        onClick = viewModel::stopTimer,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(NeonPink.copy(alpha = 0.15f))
                            .border(1.dp, NeonPink.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(Icons.Default.Stop, "Stop", tint = NeonPink, modifier = Modifier.size(24.dp))
                    }
                }

                // Start/Pause main button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(NeonBlue, NeonPurple)
                            )
                        )
                        .clickable {
                            when {
                                uiState.isRunning -> viewModel.pauseTimer()
                                uiState.isPaused -> viewModel.resumeTimer()
                                else -> viewModel.startTimer()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (uiState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        if (uiState.isRunning) "Pause" else "Start",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Orb selector
            if (uiState.availableOrbs.isNotEmpty()) {
                Text(
                    "Select Orb",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.availableOrbs) { orb ->
                        OrbSelectorItem(
                            orb = orb,
                            isSelected = orb.id == uiState.selectedOrb?.id,
                            onClick = { viewModel.selectOrb(orb) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrbSelectorItem(
    orb: Orb,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = parseColor(orb.colorHex)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) color.copy(alpha = 0.12f) else GlassWhite)
            .border(
                if (isSelected) 1.5.dp else 1.dp,
                if (isSelected) color.copy(alpha = 0.6f) else GlassBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        OrbBall(
            title = "",
            color = color,
            sizeDp = 44.dp,
            showLabel = false
        )
        Spacer(Modifier.height(4.dp))
        Text(
            orb.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) color else TextSecondary,
            maxLines = 1,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

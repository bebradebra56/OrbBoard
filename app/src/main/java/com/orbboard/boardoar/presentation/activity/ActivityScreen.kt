package com.orbboard.boardoar.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.presentation.components.GlassCard
import com.orbboard.boardoar.presentation.components.OrbBall
import com.orbboard.boardoar.ui.theme.*
import com.orbboard.boardoar.util.formatDate
import com.orbboard.boardoar.util.parseColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActivityScreen(
    paddingValues: PaddingValues,
    viewModel: ActivityViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    "Activity",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${uiState.completedOrbs.size} completed · ${uiState.archivedOrbs.size} archived",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // Tabs
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = BackgroundMedium,
                contentColor = NeonBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                        color = NeonBlue
                    )
                }
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.setTab(0) },
                    text = {
                        Text(
                            "Completed",
                            color = if (uiState.selectedTab == 0) NeonBlue else TextSecondary
                        )
                    }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.setTab(1) },
                    text = {
                        Text(
                            "Archived",
                            color = if (uiState.selectedTab == 1) NeonBlue else TextSecondary
                        )
                    }
                )
            }

            val currentList = if (uiState.selectedTab == 0) uiState.completedOrbs else uiState.archivedOrbs

            if (currentList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            if (uiState.selectedTab == 0) "✓" else "☰",
                            fontSize = androidx.compose.ui.unit.TextUnit(48f, androidx.compose.ui.unit.TextUnitType.Sp),
                            color = TextTertiary
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (uiState.selectedTab == 0) "No completed orbs yet" else "No archived orbs",
                            color = TextSecondary,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            if (uiState.selectedTab == 0) "Complete tasks to see them here" else "Archive tasks from the board",
                            color = TextTertiary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = paddingValues.calculateBottomPadding() + 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(currentList) { orb ->
                        ActivityOrbCard(
                            orb = orb,
                            showRestore = uiState.selectedTab == 1,
                            onRestore = { viewModel.restoreOrb(orb) },
                            onDelete = { viewModel.deleteOrb(orb.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityOrbCard(
    orb: Orb,
    showRestore: Boolean,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    val color = parseColor(orb.colorHex)

    GlassCard(modifier = Modifier.fillMaxWidth(), cornerRadius = 16.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrbBall(
                title = "",
                color = color,
                sizeDp = 44.dp,
                isCompleted = orb.isCompleted,
                showLabel = false
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(orb.title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (orb.isCompleted && orb.completedAt != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = NeonLime, modifier = Modifier.size(12.dp))
                            Text(formatDate(orb.completedAt), style = MaterialTheme.typography.labelSmall, color = NeonLime)
                        }
                    } else {
                        Text(formatDate(orb.createdAt), style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    }
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(orb.priority.name.let {
                                when (it) {
                                    "HIGH" -> NeonPink
                                    "MEDIUM" -> NeonYellow
                                    else -> NeonLime
                                }
                            }.copy(alpha = 0.7f))
                    )
                    Text(orb.priority.label, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                }
            }

            if (showRestore) {
                IconButton(
                    onClick = onRestore,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Unarchive, "Restore", tint = NeonBlue, modifier = Modifier.size(18.dp))
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Delete, "Delete", tint = NeonPink.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
            }
        }
    }
}

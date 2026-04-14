package com.orbboard.boardoar.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import com.orbboard.boardoar.domain.model.SubTask
import com.orbboard.boardoar.presentation.components.GlassCard
import com.orbboard.boardoar.presentation.components.OrbBall
import com.orbboard.boardoar.ui.theme.*
import com.orbboard.boardoar.util.formatDate
import com.orbboard.boardoar.util.parseColor
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrbDetailScreen(
    orbId: Long,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onFocus: () -> Unit,
    viewModel: OrbDetailViewModel = koinViewModel(parameters = { parametersOf(orbId) })
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.orb) {
        if (!uiState.isLoading && uiState.orb == null) onBack()
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
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
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
                        "Orb Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, "Edit", tint = TextSecondary)
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, "Delete", tint = NeonPink)
                    }
                }
            }

            uiState.orb?.let { orb ->
                item {
                    // Orb visual
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            OrbBall(
                                title = orb.title,
                                color = parseColor(orb.colorHex),
                                sizeDp = orb.size.dp.dp * 1.4f,
                                isCompleted = orb.isCompleted
                            )
                            Spacer(Modifier.height(12.dp))
                            if (orb.isCompleted) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(NeonLime.copy(alpha = 0.15f))
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, null, tint = NeonLime, modifier = Modifier.size(16.dp))
                                        Text("Completed", color = NeonLime, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    // Details card
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(orb.title, style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)

                            if (orb.description.isNotBlank()) {
                                Text(orb.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            }

                            HorizontalDivider(color = GlassBorder)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                DetailChip(
                                    icon = Icons.Default.Flag,
                                    label = orb.priority.label,
                                    color = when (orb.priority.name) {
                                        "HIGH" -> NeonPink
                                        "MEDIUM" -> NeonYellow
                                        else -> NeonLime
                                    }
                                )
                                DetailChip(
                                    icon = Icons.Default.Category,
                                    label = orb.size.label,
                                    color = NeonBlue
                                )
                            }

                            orb.dueDate?.let { due ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CalendarToday, null, tint = NeonYellow, modifier = Modifier.size(16.dp))
                                    Text("Due ${formatDate(due)}", style = MaterialTheme.typography.bodyMedium, color = NeonYellow)
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Schedule, null, tint = TextTertiary, modifier = Modifier.size(16.dp))
                                Text("Created ${formatDate(orb.createdAt)}", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                            }
                        }
                    }
                }

                // Sub-tasks
                item {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Checklist",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "${uiState.subTasks.count { it.isCompleted }}/${uiState.subTasks.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                items(uiState.subTasks) { subTask ->
                    SubTaskItem(
                        subTask = subTask,
                        onToggle = { viewModel.toggleSubTask(subTask) },
                        onDelete = { viewModel.deleteSubTask(subTask) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)
                    )
                }

                item {
                    // Add sub-task
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.newSubTaskText,
                            onValueChange = viewModel::onNewSubTaskTextChange,
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Add checklist item...", color = TextTertiary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonBlue.copy(alpha = 0.5f),
                                unfocusedBorderColor = GlassBorder,
                                focusedContainerColor = SurfaceDark,
                                unfocusedContainerColor = SurfaceDark,
                                cursorColor = NeonBlue
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        IconButton(
                            onClick = viewModel::addSubTask,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NeonBlue.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Add, "Add", tint = NeonBlue)
                        }
                    }
                }

                // Action buttons
                item {
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (!orb.isCompleted) {
                            Button(
                                onClick = viewModel::completeOrb,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonLime)
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = BackgroundDark, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Complete", color = BackgroundDark, fontWeight = FontWeight.Bold)
                            }
                        }
                        Button(
                            onClick = onFocus,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                        ) {
                            Icon(Icons.Default.Timer, null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Focus", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = SurfaceDark,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Delete Orb?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("This orb will be permanently deleted.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteOrb()
                        showDeleteConfirm = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun DetailChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SubTaskItem(
    subTask: SubTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth(), cornerRadius = 12.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = subTask.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = NeonLime,
                    uncheckedColor = TextTertiary,
                    checkmarkColor = BackgroundDark
                )
            )
            Text(
                text = subTask.title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (subTask.isCompleted) TextTertiary else TextPrimary,
                modifier = Modifier.weight(1f),
                textDecoration = if (subTask.isCompleted)
                    androidx.compose.ui.text.style.TextDecoration.LineThrough
                else null
            )
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Close, "Delete", tint = TextTertiary, modifier = Modifier.size(14.dp))
            }
        }
    }
}

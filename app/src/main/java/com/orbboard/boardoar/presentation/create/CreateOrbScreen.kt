package com.orbboard.boardoar.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.orbboard.boardoar.domain.model.OrbSize
import com.orbboard.boardoar.domain.model.Priority
import com.orbboard.boardoar.presentation.components.GlassCard
import com.orbboard.boardoar.presentation.components.OrbBall
import com.orbboard.boardoar.ui.theme.*
import com.orbboard.boardoar.ui.theme.OrbColors
import com.orbboard.boardoar.util.formatDate
import com.orbboard.boardoar.util.parseColor
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrbScreen(
    orbId: Long,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CreateOrbViewModel = koinViewModel(parameters = { parametersOf(orbId) })
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.savedSuccessfully) {
        if (uiState.savedSuccessfully) onSaved()
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

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
                .statusBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, "Close", tint = TextSecondary)
                }
                Text(
                    if (uiState.isEditMode) "Edit Orb" else "Create Orb",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Orb Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    OrbBall(
                        title = uiState.title.ifBlank { "Orb" },
                        color = parseColor(uiState.colorHex),
                        sizeDp = uiState.size.dp.dp
                    )
                }

                // Title
                OrbTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    label = "Title",
                    icon = Icons.Default.Title,
                    isError = uiState.titleError != null,
                    supportingText = uiState.titleError
                )

                // Description
                OrbTextField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = "Description (optional)",
                    icon = Icons.Default.Description,
                    minLines = 3
                )

                // Category
                SectionLabel("Category")
                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown,
                    onExpandedChange = { showCategoryDropdown = it }
                ) {
                    OutlinedTextField(
                        value = uiState.categories.find { it.id == uiState.selectedCategoryId }?.name
                            ?: "Select category",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = orbTextFieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        leadingIcon = {
                            val cat = uiState.categories.find { it.id == uiState.selectedCategoryId }
                            if (cat != null) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(parseColor(cat.colorHex))
                                )
                            } else {
                                Icon(Icons.Default.Category, null, tint = TextTertiary, modifier = Modifier.size(20.dp))
                            }
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier.background(SurfaceDark)
                    ) {
                        uiState.categories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(parseColor(category.colorHex))
                                        )
                                        Text(category.name, color = TextPrimary)
                                    }
                                },
                                onClick = {
                                    viewModel.onCategorySelected(category.id)
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                // Due date
                SectionLabel("Due Date")
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SurfaceDark,
                        contentColor = TextPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        null,
                        tint = if (uiState.dueDate != null) NeonBlue else TextTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = uiState.dueDate?.let { formatDate(it) } ?: "Set due date",
                        color = if (uiState.dueDate != null) TextPrimary else TextTertiary
                    )
                    Spacer(Modifier.weight(1f))
                    if (uiState.dueDate != null) {
                        IconButton(
                            onClick = { viewModel.onDueDateChange(null) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Clear, null, tint = TextTertiary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Priority
                SectionLabel("Priority")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Priority.entries.forEach { priority ->
                        val isSelected = uiState.priority == priority
                        val chipColor = when (priority) {
                            Priority.LOW -> NeonLime
                            Priority.MEDIUM -> NeonYellow
                            Priority.HIGH -> NeonPink
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onPriorityChange(priority) },
                            label = { Text(priority.label, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = chipColor.copy(alpha = 0.18f),
                                selectedLabelColor = chipColor,
                                containerColor = SurfaceDark,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = chipColor.copy(alpha = 0.5f),
                                borderColor = GlassBorder
                            )
                        )
                    }
                }

                // Size
                SectionLabel("Size")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OrbSize.entries.forEach { size ->
                        val isSelected = uiState.size == size
                        GlassCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.onSizeChange(size) }
                                .then(
                                    if (isSelected) Modifier.border(
                                        1.5.dp,
                                        NeonBlue,
                                        RoundedCornerShape(14.dp)
                                    ) else Modifier
                                ),
                            cornerRadius = 14.dp,
                            backgroundColor = if (isSelected) NeonBlue.copy(alpha = 0.12f) else GlassWhite,
                            borderColor = if (isSelected) NeonBlue.copy(alpha = 0.4f) else GlassBorder
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                OrbBall(
                                    title = "",
                                    color = parseColor(uiState.colorHex),
                                    sizeDp = size.dp.dp * 0.55f,
                                    showLabel = false
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    size.label,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) NeonBlue else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // Color picker
                SectionLabel("Orb Color")
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OrbColors.forEach { hex ->
                        val c = parseColor(hex)
                        val isSelected = uiState.colorHex == hex
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(c)
                                .then(
                                    if (isSelected) Modifier.border(2.5.dp, Color.White, CircleShape)
                                    else Modifier.border(1.dp, c.copy(alpha = 0.4f), CircleShape)
                                )
                                .clickable { viewModel.onColorChange(hex) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            // Save button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Button(
                    onClick = viewModel::saveOrb,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = BackgroundDark,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Add, null, tint = BackgroundDark, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (uiState.isEditMode) "Save Changes" else "Create Orb",
                            color = BackgroundDark,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        OrbDatePickerDialog(
            initialDate = uiState.dueDate,
            onDateSelected = { viewModel.onDueDateChange(it); showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrbDatePickerDialog(
    initialDate: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate ?: System.currentTimeMillis()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
            }) {
                Text("Confirm", color = NeonBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = SurfaceDark
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = SurfaceDark,
                titleContentColor = TextPrimary,
                headlineContentColor = NeonBlue,
                weekdayContentColor = TextSecondary,
                subheadContentColor = TextSecondary,
                navigationContentColor = TextPrimary,
                yearContentColor = TextPrimary,
                currentYearContentColor = NeonBlue,
                selectedYearContainerColor = NeonBlue,
                dayContentColor = TextPrimary,
                selectedDayContainerColor = NeonBlue,
                todayContentColor = NeonBlue,
                todayDateBorderColor = NeonBlue.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = TextSecondary,
        fontWeight = FontWeight.SemiBold
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrbTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    minLines: Int = 1,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextTertiary) },
        leadingIcon = { Icon(icon, null, tint = TextTertiary, modifier = Modifier.size(20.dp)) },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        isError = isError,
        supportingText = supportingText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
        colors = orbTextFieldColors(),
        shape = RoundedCornerShape(14.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun orbTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = NeonBlue.copy(alpha = 0.6f),
    unfocusedBorderColor = GlassBorder,
    focusedContainerColor = SurfaceDark,
    unfocusedContainerColor = SurfaceDark,
    cursorColor = NeonBlue,
    focusedLabelColor = NeonBlue,
    unfocusedLabelColor = TextTertiary
)

package com.orbboard.boardoar.presentation.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.orbboard.boardoar.presentation.components.GlassCard
import com.orbboard.boardoar.ui.theme.*
import com.orbboard.boardoar.ui.theme.OrbColors
import com.orbboard.boardoar.util.parseColor
import org.koin.androidx.compose.koinViewModel

private val categoryIcons = mapOf(
    "work" to Icons.Default.Work,
    "person" to Icons.Default.Person,
    "lightbulb" to Icons.Default.Lightbulb,
    "shopping_cart" to Icons.Default.ShoppingCart,
    "star" to Icons.Default.Star,
    "home" to Icons.Default.Home,
    "fitness_center" to Icons.Default.FitnessCenter,
    "book" to Icons.Default.MenuBook,
    "music" to Icons.Default.MusicNote,
    "travel" to Icons.Default.Flight
)

@Composable
fun CategoriesScreen(
    paddingValues: PaddingValues,
    onCategoryClick: (Long) -> Unit,
    viewModel: CategoriesViewModel = koinViewModel()
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
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Categories",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${uiState.categoriesWithCount.size} groups",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                FloatingActionButton(
                    onClick = viewModel::showAddDialog,
                    containerColor = NeonPurple,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp),
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(Icons.Default.Add, "Add Category", modifier = Modifier.size(20.dp))
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.categoriesWithCount) { item ->
                    CategoryCard(
                        item = item,
                        onClick = { onCategoryClick(item.category.id) },
                        onEdit = { viewModel.showEditDialog(item.category) },
                        onDelete = { viewModel.deleteCategory(item.category) }
                    )
                }

                if (uiState.categoriesWithCount.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("◈", fontSize = androidx.compose.ui.unit.TextUnit(48f, androidx.compose.ui.unit.TextUnitType.Sp), color = TextTertiary)
                                Spacer(Modifier.height(16.dp))
                                Text("No categories yet", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
                                Text("Tap + to add one", color = TextTertiary, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        AddCategoryDialog(
            isEdit = uiState.editingCategory != null,
            name = uiState.dialogName,
            selectedColor = uiState.dialogColor,
            selectedIcon = uiState.dialogIcon,
            nameError = uiState.nameError,
            onNameChange = viewModel::onDialogNameChange,
            onColorChange = viewModel::onDialogColorChange,
            onIconChange = viewModel::onDialogIconChange,
            onConfirm = viewModel::saveCategory,
            onDismiss = viewModel::dismissDialog
        )
    }
}

@Composable
private fun CategoryCard(
    item: CategoryWithCount,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val color = parseColor(item.category.colorHex)
    val icon = categoryIcons[item.category.iconName] ?: Icons.Default.Category
    val progress = if (item.totalCount > 0) item.completedCount.toFloat() / item.totalCount else 0f

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        cornerRadius = 18.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f))
                    .border(1.dp, color.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, item.category.name, tint = color, modifier = Modifier.size(26.dp))
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.category.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${item.totalCount} orbs · ${item.completedCount} done",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(50)),
                    color = color,
                    trackColor = color.copy(alpha = 0.2f)
                )
            }

            Spacer(Modifier.width(8.dp))

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, "Options", tint = TextTertiary)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(SurfaceDark)
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit", color = TextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = NeonBlue, modifier = Modifier.size(18.dp)) },
                        onClick = { showMenu = false; onEdit() }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = NeonPink) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = NeonPink, modifier = Modifier.size(18.dp)) },
                        onClick = { showMenu = false; onDelete() }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddCategoryDialog(
    isEdit: Boolean,
    name: String,
    selectedColor: String,
    selectedIcon: String,
    nameError: String?,
    onNameChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onIconChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                if (isEdit) "Edit Category" else "Add Category",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Name", color = TextTertiary) },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonPurple.copy(alpha = 0.6f),
                        unfocusedBorderColor = GlassBorder,
                        focusedContainerColor = BackgroundMedium,
                        unfocusedContainerColor = BackgroundMedium,
                        cursorColor = NeonPurple,
                        focusedLabelColor = NeonPurple
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Color", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OrbColors.take(8).forEach { hex ->
                        val c = parseColor(hex)
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(c)
                                .then(
                                    if (selectedColor == hex) Modifier.border(2.dp, Color.White, CircleShape)
                                    else Modifier
                                )
                                .clickable { onColorChange(hex) }
                        )
                    }
                }

                Text("Icon", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categoryIcons.entries.take(8).forEach { (key, icon) ->
                        val isSelected = selectedIcon == key
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NeonPurple.copy(alpha = 0.2f) else GlassWhite)
                                .border(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) NeonPurple else GlassBorder,
                                    CircleShape
                                )
                                .clickable { onIconChange(key) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = if (isSelected) NeonPurple else TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isEdit) "Save" else "Add", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

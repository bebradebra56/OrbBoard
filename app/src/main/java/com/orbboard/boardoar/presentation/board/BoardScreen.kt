package com.orbboard.boardoar.presentation.board

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.orbboard.boardoar.data.preferences.AppPreferences
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.domain.model.OrbSize
import com.orbboard.boardoar.domain.model.Category
import com.orbboard.boardoar.presentation.components.OrbBall
import com.orbboard.boardoar.ui.theme.*
import com.orbboard.boardoar.util.parseColor
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.math.roundToInt

@Composable
fun BoardScreen(
    paddingValues: PaddingValues,
    onCreateOrb: () -> Unit,
    onOrbClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    onStatsClick: () -> Unit,
    viewModel: BoardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var contextMenuOrbId by remember { mutableStateOf<Long?>(null) }
    val haptic = LocalHapticFeedback.current

    val preferences: AppPreferences = koinInject()
    val boardStyle by preferences.boardStyle.collectAsState(initial = "Grid")

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
            BoardTopBar(
                onSearchClick = onSearchClick,
                onStatsClick = onStatsClick
            )

            if (uiState.categories.isNotEmpty()) {
                CategoryFilterRow(
                    categories = uiState.categories,
                    selectedId = uiState.selectedCategoryId,
                    onSelect = { viewModel.setFilter(it) }
                )
            }

            OrbBoardCanvas(
                orbs = uiState.orbs,
                categories = uiState.categories,
                boardStyle = boardStyle,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = paddingValues.calculateBottomPadding()),
                onOrbClick = onOrbClick,
                onOrbLongClick = { orbId ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    contextMenuOrbId = orbId
                },
                onOrbPositionChange = { id, x, y ->
                    viewModel.updateOrbPosition(id, x, y)
                }
            )
        }

        FloatingActionButton(
            onClick = onCreateOrb,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 24.dp,
                    bottom = paddingValues.calculateBottomPadding() + 24.dp
                ),
            containerColor = NeonBlue,
            contentColor = BackgroundDark,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Orb", modifier = Modifier.size(26.dp))
        }

        contextMenuOrbId?.let { orbId ->
            OrbContextMenu(
                onDismiss = { contextMenuOrbId = null },
                onComplete = {
                    viewModel.completeOrb(orbId)
                    contextMenuOrbId = null
                },
                onArchive = {
                    viewModel.archiveOrb(orbId)
                    contextMenuOrbId = null
                },
                onDelete = {
                    viewModel.deleteOrb(orbId)
                    contextMenuOrbId = null
                }
            )
        }
    }
}

@Composable
private fun BoardTopBar(
    onSearchClick: () -> Unit,
    onStatsClick: () -> Unit
) {
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
                "Orb Board",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Your visual workspace",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, "Search", tint = TextSecondary)
            }
            IconButton(onClick = onStatsClick) {
                Icon(Icons.Default.BarChart, "Stats", tint = TextSecondary)
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<Category>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 4.dp)
    ) {
        item {
            FilterChip(
                selected = selectedId == null,
                onClick = { onSelect(null) },
                label = { Text("All") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonBlue.copy(alpha = 0.18f),
                    selectedLabelColor = NeonBlue,
                    containerColor = SurfaceDark,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedId == null,
                    selectedBorderColor = NeonBlue.copy(alpha = 0.4f),
                    borderColor = GlassBorder
                )
            )
        }
        items(categories) { category ->
            val color = parseColor(category.colorHex)
            FilterChip(
                selected = selectedId == category.id,
                onClick = {
                    onSelect(if (selectedId == category.id) null else category.id)
                },
                label = { Text(category.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = color.copy(alpha = 0.18f),
                    selectedLabelColor = color,
                    containerColor = SurfaceDark,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedId == category.id,
                    selectedBorderColor = color.copy(alpha = 0.4f),
                    borderColor = GlassBorder
                )
            )
        }
    }
}

@Composable
private fun OrbBoardCanvas(
    orbs: List<Orb>,
    categories: List<Category>,
    boardStyle: String,
    modifier: Modifier = Modifier,
    onOrbClick: (Long) -> Unit,
    onOrbLongClick: (Long) -> Unit,
    onOrbPositionChange: (Long, Float, Float) -> Unit
) {
    var boardWidth by remember { mutableIntStateOf(1) }
    var boardHeight by remember { mutableIntStateOf(1) }
    var draggingOrbId by remember { mutableStateOf<Long?>(null) }

    // Capture theme colors in composable context before Canvas DrawScope
    val gridLineColor = LocalOrbColors.current.gridLine
    val gridDotColor = LocalOrbColors.current.gridDot

    Box(
        modifier = modifier.onSizeChanged {
            boardWidth = it.width.coerceAtLeast(1)
            boardHeight = it.height.coerceAtLeast(1)
        }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawBoardBackground(boardStyle, gridLineColor, gridDotColor)
        }

        val sortedOrbs = orbs.sortedBy { if (it.id == draggingOrbId) 1 else 0 }

        sortedOrbs.forEach { orb ->
            val orbColor = parseColor(orb.colorHex)
            val sizeDp = orb.size.dp.dp

            DraggableOrb(
                orb = orb,
                color = orbColor,
                sizeDp = sizeDp,
                boardWidth = boardWidth,
                boardHeight = boardHeight,
                isDraggingGlobal = draggingOrbId == orb.id,
                onClick = { onOrbClick(orb.id) },
                onLongClick = { onOrbLongClick(orb.id) },
                onDragStarted = { draggingOrbId = orb.id },
                onDragEnded = { draggingOrbId = null },
                onPositionChange = { x, y -> onOrbPositionChange(orb.id, x, y) }
            )
        }

        if (orbs.isEmpty()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("✦", fontSize = 52.sp, color = TextTertiary)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Your board is empty",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tap + to create your first Orb",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )
            }
        }
    }
}

@Composable
private fun DraggableOrb(
    orb: Orb,
    color: Color,
    sizeDp: Dp,
    boardWidth: Int,
    boardHeight: Int,
    isDraggingGlobal: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDragStarted: () -> Unit,
    onDragEnded: () -> Unit,
    onPositionChange: (Float, Float) -> Unit
) {
    val density = LocalDensity.current
    val sizePx = with(density) { sizeDp.toPx() }
    val totalSizePx = with(density) { (sizeDp + 28.dp).toPx() }

    var posX by remember(orb.id, boardWidth) {
        mutableFloatStateOf(
            if (boardWidth > 1) orb.posX * boardWidth else orb.posX * 800f
        )
    }
    var posY by remember(orb.id, boardHeight) {
        mutableFloatStateOf(
            if (boardHeight > 1) orb.posY * boardHeight else orb.posY * 600f
        )
    }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(orb.posX, orb.posY, boardWidth, boardHeight) {
        if (!isDragging && boardWidth > 1 && boardHeight > 1) {
            posX = orb.posX * boardWidth
            posY = orb.posY * boardHeight
        }
    }

    val orbElevation by animateFloatAsState(
        targetValue = if (isDragging) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "elevation"
    )

    OrbBall(
        title = orb.title,
        color = color,
        sizeDp = sizeDp,
        isCompleted = orb.isCompleted,
        isDragging = isDragging,
        modifier = Modifier
            .offset {
                IntOffset(
                    (posX - totalSizePx / 2).roundToInt(),
                    (posY - totalSizePx / 2).roundToInt()
                )
            }
            .graphicsLayer { shadowElevation = orbElevation * 24f }
            .pointerInput(orb.id) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onLongClick() }
                )
            }
            .pointerInput(orb.id, boardWidth, boardHeight) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        onDragStarted()
                    },
                    onDragEnd = {
                        isDragging = false
                        onDragEnded()
                        if (boardWidth > 1 && boardHeight > 1) {
                            onPositionChange(
                                (posX / boardWidth).coerceIn(0.02f, 0.98f),
                                (posY / boardHeight).coerceIn(0.02f, 0.98f)
                            )
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                        onDragEnded()
                    }
                ) { change, dragAmount ->
                    change.consume()
                    posX = (posX + dragAmount.x).coerceIn(
                        totalSizePx / 2,
                        boardWidth - totalSizePx / 2
                    )
                    posY = (posY + dragAmount.y).coerceIn(
                        totalSizePx / 2,
                        boardHeight - totalSizePx / 2
                    )
                }
            }
    )
}

@Composable
private fun OrbContextMenu(
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceDark,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text("Orb Actions", color = TextPrimary, fontWeight = FontWeight.SemiBold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onComplete, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = NeonLime, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Mark Complete", color = NeonLime)
                    }
                }
                TextButton(onClick = onArchive, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Archive, null, tint = NeonYellow, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Archive", color = NeonYellow)
                    }
                }
                TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Delete, null, tint = NeonPink, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Delete", color = NeonPink)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

// DrawScope helper — receives colors as parameters since DrawScope is not @Composable
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBoardBackground(
    style: String,
    lineColor: Color,
    dotColor: Color
) {
    val gridSpacing = 56.dp.toPx()
    when (style) {
        "Grid" -> {
            var x = 0f
            while (x <= size.width) {
                drawLine(color = lineColor, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1f)
                x += gridSpacing
            }
            var y = 0f
            while (y <= size.height) {
                drawLine(color = lineColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
                y += gridSpacing
            }
            x = 0f
            while (x <= size.width) {
                var yDot = 0f
                while (yDot <= size.height) {
                    drawCircle(color = dotColor, radius = 2f, center = Offset(x, yDot))
                    yDot += gridSpacing
                }
                x += gridSpacing
            }
        }
        "Dots" -> {
            var x = 0f
            while (x <= size.width) {
                var y = 0f
                while (y <= size.height) {
                    drawCircle(color = dotColor.copy(alpha = 0.75f), radius = 2.8f, center = Offset(x, y))
                    y += gridSpacing
                }
                x += gridSpacing
            }
        }
        // "None" → nothing drawn
    }
}

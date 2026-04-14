package com.orbboard.boardoar.presentation.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbboard.boardoar.domain.model.Category
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.domain.repository.CategoryRepository
import com.orbboard.boardoar.domain.repository.OrbRepository
import com.orbboard.boardoar.util.formatDayName
import com.orbboard.boardoar.util.startOfDay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategoryStat(
    val category: Category,
    val count: Int,
    val completedCount: Int,
    val fraction: Float
)

data class DayStat(
    val dayName: String,
    val count: Int
)

data class StatsUiState(
    val completedCount: Int = 0,
    val pendingCount: Int = 0,
    val totalCount: Int = 0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val weeklyData: List<DayStat> = emptyList(),
    val focusSessionCount: Int = 0
)

class StatsViewModel(
    private val orbRepository: OrbRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                orbRepository.getAllOrbs(),
                categoryRepository.getAllCategories()
            ) { orbs, categories ->
                buildStats(orbs, categories)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    private fun buildStats(allOrbs: List<Orb>, categories: List<Category>): StatsUiState {
        val completed = allOrbs.filter { it.isCompleted }
        val pending = allOrbs.filter { !it.isCompleted && !it.isArchived }
        val total = allOrbs.size

        // Category breakdown
        val catStats = categories.mapNotNull { cat ->
            val orbsInCat = allOrbs.filter { it.categoryId == cat.id && !it.isArchived }
            if (orbsInCat.isEmpty() && total > 0) return@mapNotNull null
            CategoryStat(
                category = cat,
                count = orbsInCat.size,
                completedCount = orbsInCat.count { it.isCompleted },
                fraction = if (total > 0) orbsInCat.size.toFloat() / allOrbs.filter { !it.isArchived }.size.coerceAtLeast(1) else 0f
            )
        }.sortedByDescending { it.count }

        // Weekly data (last 7 days)
        val weeklyData = (6 downTo 0).map { daysAgo ->
            val dayStart = startOfDay(daysAgo)
            val dayEnd = dayStart + 24 * 60 * 60 * 1000L
            val count = completed.count { orb ->
                orb.completedAt != null && orb.completedAt >= dayStart && orb.completedAt < dayEnd
            }
            DayStat(
                dayName = formatDayName(dayStart),
                count = count
            )
        }

        return StatsUiState(
            completedCount = completed.size,
            pendingCount = pending.size,
            totalCount = allOrbs.filter { !it.isArchived }.size,
            categoryStats = catStats,
            weeklyData = weeklyData
        )
    }
}

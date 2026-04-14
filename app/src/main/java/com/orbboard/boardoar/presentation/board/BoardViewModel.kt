package com.orbboard.boardoar.presentation.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbboard.boardoar.domain.model.Category
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.domain.repository.CategoryRepository
import com.orbboard.boardoar.domain.repository.OrbRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BoardUiState(
    val orbs: List<Orb> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val isLoading: Boolean = true
)

class BoardViewModel(
    private val orbRepository: OrbRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<BoardUiState> = combine(
        orbRepository.getAllOrbs(),
        categoryRepository.getAllCategories(),
        _selectedCategoryId
    ) { orbs, categories, selectedId ->
        val filteredOrbs = if (selectedId != null) {
            orbs.filter { it.categoryId == selectedId }
        } else {
            orbs
        }
        BoardUiState(
            orbs = filteredOrbs,
            categories = categories,
            selectedCategoryId = selectedId,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BoardUiState()
    )

    fun setFilter(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun updateOrbPosition(orbId: Long, posX: Float, posY: Float) {
        viewModelScope.launch {
            orbRepository.updateOrbPosition(orbId, posX, posY)
        }
    }

    fun deleteOrb(orbId: Long) {
        viewModelScope.launch {
            orbRepository.deleteOrb(orbId)
        }
    }

    fun completeOrb(orbId: Long) {
        viewModelScope.launch {
            orbRepository.completeOrb(orbId)
        }
    }

    fun archiveOrb(orbId: Long) {
        viewModelScope.launch {
            val orb = orbRepository.getOrbById(orbId) ?: return@launch
            orbRepository.updateOrb(orb.copy(isArchived = true))
        }
    }
}

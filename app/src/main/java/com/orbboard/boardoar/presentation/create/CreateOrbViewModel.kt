package com.orbboard.boardoar.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbboard.boardoar.domain.model.Category
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.domain.model.OrbSize
import com.orbboard.boardoar.domain.model.Priority
import com.orbboard.boardoar.domain.repository.CategoryRepository
import com.orbboard.boardoar.domain.repository.OrbRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

data class CreateOrbUiState(
    val isEditMode: Boolean = false,
    val title: String = "",
    val description: String = "",
    val selectedCategoryId: Long = -1L,
    val dueDate: Long? = null,
    val priority: Priority = Priority.MEDIUM,
    val size: OrbSize = OrbSize.MEDIUM,
    val colorHex: String = "#7A5CFF",
    val categories: List<Category> = emptyList(),
    val isSaving: Boolean = false,
    val savedSuccessfully: Boolean = false,
    val titleError: String? = null
)

class CreateOrbViewModel(
    private val orbRepository: OrbRepository,
    private val categoryRepository: CategoryRepository,
    private val orbId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateOrbUiState(isEditMode = orbId > 0))
    val uiState: StateFlow<CreateOrbUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().first().let { categories ->
                _uiState.update { it.copy(categories = categories) }
                if (categories.isNotEmpty() && _uiState.value.selectedCategoryId == -1L) {
                    _uiState.update {
                        it.copy(
                            selectedCategoryId = categories.first().id,
                            colorHex = categories.first().colorHex
                        )
                    }
                }
            }
        }
        if (orbId > 0) {
            loadOrb(orbId)
        }

        // Observe categories for live updates
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    private fun loadOrb(id: Long) {
        viewModelScope.launch {
            orbRepository.getOrbById(id)?.let { orb ->
                _uiState.update {
                    it.copy(
                        title = orb.title,
                        description = orb.description,
                        selectedCategoryId = orb.categoryId,
                        dueDate = orb.dueDate,
                        priority = orb.priority,
                        size = orb.size,
                        colorHex = orb.colorHex
                    )
                }
            }
        }
    }

    fun onTitleChange(value: String) =
        _uiState.update { it.copy(title = value, titleError = null) }

    fun onDescriptionChange(value: String) =
        _uiState.update { it.copy(description = value) }

    fun onCategorySelected(categoryId: Long) {
        val category = _uiState.value.categories.find { it.id == categoryId }
        _uiState.update {
            it.copy(
                selectedCategoryId = categoryId,
                colorHex = category?.colorHex ?: it.colorHex
            )
        }
    }

    fun onDueDateChange(timestamp: Long?) =
        _uiState.update { it.copy(dueDate = timestamp) }

    fun onPriorityChange(priority: Priority) =
        _uiState.update { it.copy(priority = priority) }

    fun onSizeChange(size: OrbSize) =
        _uiState.update { it.copy(size = size) }

    fun onColorChange(hex: String) =
        _uiState.update { it.copy(colorHex = hex) }

    fun saveOrb() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }
        if (state.selectedCategoryId == -1L && state.categories.isEmpty()) {
            _uiState.update { it.copy(titleError = "Please create a category first") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val categoryId = if (state.selectedCategoryId == -1L)
                state.categories.firstOrNull()?.id ?: 1L
            else
                state.selectedCategoryId

            if (state.isEditMode && orbId > 0) {
                val existing = orbRepository.getOrbById(orbId) ?: return@launch
                orbRepository.updateOrb(
                    existing.copy(
                        title = state.title.trim(),
                        description = state.description.trim(),
                        categoryId = categoryId,
                        dueDate = state.dueDate,
                        priority = state.priority,
                        size = state.size,
                        colorHex = state.colorHex
                    )
                )
            } else {
                val newOrb = Orb(
                    title = state.title.trim(),
                    description = state.description.trim(),
                    categoryId = categoryId,
                    dueDate = state.dueDate,
                    priority = state.priority,
                    size = state.size,
                    colorHex = state.colorHex,
                    posX = 0.1f + Random.nextFloat() * 0.8f,
                    posY = 0.1f + Random.nextFloat() * 0.8f
                )
                orbRepository.insertOrb(newOrb)
            }
            _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
        }
    }
}

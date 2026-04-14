package com.orbboard.boardoar.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbboard.boardoar.domain.model.Category
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.domain.repository.CategoryRepository
import com.orbboard.boardoar.domain.repository.OrbRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategoryWithCount(
    val category: Category,
    val totalCount: Int,
    val completedCount: Int
)

data class CategoriesUiState(
    val categoriesWithCount: List<CategoryWithCount> = emptyList(),
    val showAddDialog: Boolean = false,
    val editingCategory: Category? = null,
    val dialogName: String = "",
    val dialogColor: String = "#7A5CFF",
    val dialogIcon: String = "work",
    val nameError: String? = null
)

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository,
    private val orbRepository: OrbRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                categoryRepository.getAllCategories(),
                orbRepository.getAllOrbs()
            ) { categories, orbs ->
                categories.map { category ->
                    val categoryOrbs = orbs.filter { it.categoryId == category.id }
                    CategoryWithCount(
                        category = category,
                        totalCount = categoryOrbs.size,
                        completedCount = categoryOrbs.count { it.isCompleted }
                    )
                }
            }.collect { list ->
                _uiState.update { it.copy(categoriesWithCount = list) }
            }
        }
    }

    fun showAddDialog() = _uiState.update {
        it.copy(showAddDialog = true, editingCategory = null, dialogName = "", dialogColor = "#7A5CFF", dialogIcon = "work", nameError = null)
    }

    fun showEditDialog(category: Category) = _uiState.update {
        it.copy(showAddDialog = true, editingCategory = category, dialogName = category.name, dialogColor = category.colorHex, dialogIcon = category.iconName, nameError = null)
    }

    fun dismissDialog() = _uiState.update { it.copy(showAddDialog = false, editingCategory = null, nameError = null) }

    fun onDialogNameChange(name: String) = _uiState.update { it.copy(dialogName = name, nameError = null) }
    fun onDialogColorChange(color: String) = _uiState.update { it.copy(dialogColor = color) }
    fun onDialogIconChange(icon: String) = _uiState.update { it.copy(dialogIcon = icon) }

    fun saveCategory() {
        val state = _uiState.value
        if (state.dialogName.isBlank()) {
            _uiState.update { it.copy(nameError = "Name is required") }
            return
        }
        viewModelScope.launch {
            if (state.editingCategory != null) {
                categoryRepository.updateCategory(
                    state.editingCategory.copy(
                        name = state.dialogName.trim(),
                        colorHex = state.dialogColor,
                        iconName = state.dialogIcon
                    )
                )
            } else {
                categoryRepository.insertCategory(
                    Category(
                        name = state.dialogName.trim(),
                        colorHex = state.dialogColor,
                        iconName = state.dialogIcon
                    )
                )
            }
            dismissDialog()
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category.id)
        }
    }
}

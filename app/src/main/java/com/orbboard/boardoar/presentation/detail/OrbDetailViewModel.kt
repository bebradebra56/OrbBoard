package com.orbboard.boardoar.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbboard.boardoar.domain.model.Category
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.domain.model.SubTask
import com.orbboard.boardoar.domain.repository.CategoryRepository
import com.orbboard.boardoar.domain.repository.FocusRepository
import com.orbboard.boardoar.domain.repository.OrbRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OrbDetailUiState(
    val orb: Orb? = null,
    val category: Category? = null,
    val subTasks: List<SubTask> = emptyList(),
    val newSubTaskText: String = "",
    val isLoading: Boolean = true
)

class OrbDetailViewModel(
    private val orbRepository: OrbRepository,
    private val focusRepository: FocusRepository,
    private val orbId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrbDetailUiState())
    val uiState: StateFlow<OrbDetailUiState> = _uiState.asStateFlow()

    init {
        if (orbId > 0) {
            viewModelScope.launch {
                focusRepository.getSubTasksByOrb(orbId).collect { subTasks ->
                    _uiState.update { it.copy(subTasks = subTasks, isLoading = false) }
                }
            }
            viewModelScope.launch {
                val orb = orbRepository.getOrbById(orbId)
                _uiState.update { it.copy(orb = orb, isLoading = false) }
            }
        }
    }

    fun onNewSubTaskTextChange(text: String) =
        _uiState.update { it.copy(newSubTaskText = text) }

    fun addSubTask() {
        val text = _uiState.value.newSubTaskText.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            focusRepository.insertSubTask(SubTask(orbId = orbId, title = text))
            _uiState.update { it.copy(newSubTaskText = "") }
        }
    }

    fun toggleSubTask(subTask: SubTask) {
        viewModelScope.launch {
            focusRepository.updateSubTask(subTask.copy(isCompleted = !subTask.isCompleted))
        }
    }

    fun deleteSubTask(subTask: SubTask) {
        viewModelScope.launch {
            focusRepository.deleteSubTask(subTask.id)
        }
    }

    fun completeOrb() {
        viewModelScope.launch {
            orbRepository.completeOrb(orbId)
            val updated = orbRepository.getOrbById(orbId)
            _uiState.update { it.copy(orb = updated) }
        }
    }

    fun deleteOrb() {
        viewModelScope.launch {
            orbRepository.deleteOrb(orbId)
        }
    }
}

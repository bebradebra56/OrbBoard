package com.orbboard.boardoar.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.domain.repository.CategoryRepository
import com.orbboard.boardoar.domain.repository.OrbRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ActivityUiState(
    val completedOrbs: List<Orb> = emptyList(),
    val archivedOrbs: List<Orb> = emptyList(),
    val selectedTab: Int = 0
)

class ActivityViewModel(
    private val orbRepository: OrbRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            orbRepository.getCompletedOrbs().collect { orbs ->
                _uiState.update { it.copy(completedOrbs = orbs) }
            }
        }
        viewModelScope.launch {
            orbRepository.getArchivedOrbs().collect { orbs ->
                _uiState.update { it.copy(archivedOrbs = orbs) }
            }
        }
    }

    fun setTab(tab: Int) = _uiState.update { it.copy(selectedTab = tab) }

    fun restoreOrb(orb: Orb) {
        viewModelScope.launch {
            orbRepository.updateOrb(orb.copy(isArchived = false))
        }
    }

    fun deleteOrb(orbId: Long) {
        viewModelScope.launch {
            orbRepository.deleteOrb(orbId)
        }
    }
}

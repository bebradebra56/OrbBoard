package com.orbboard.boardoar.presentation.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbboard.boardoar.domain.model.FocusSession
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.domain.repository.FocusRepository
import com.orbboard.boardoar.domain.repository.OrbRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class FocusUiState(
    val selectedOrb: Orb? = null,
    val availableOrbs: List<Orb> = emptyList(),
    val durationMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false
)

class FocusViewModel(
    private val orbRepository: OrbRepository,
    private val focusRepository: FocusRepository,
    private val initialOrbId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            orbRepository.getAllOrbs().collect { orbs ->
                val pendingOrbs = orbs.filter { !it.isCompleted && !it.isArchived }
                val selected = if (initialOrbId > 0) pendingOrbs.find { it.id == initialOrbId }
                else _uiState.value.selectedOrb ?: pendingOrbs.firstOrNull()
                _uiState.update { it.copy(availableOrbs = pendingOrbs, selectedOrb = selected ?: it.selectedOrb) }
            }
        }
    }

    fun selectOrb(orb: Orb) = _uiState.update { it.copy(selectedOrb = orb) }

    fun setDuration(minutes: Int) {
        if (_uiState.value.isRunning) return
        _uiState.update { it.copy(durationMinutes = minutes, remainingSeconds = minutes * 60, isCompleted = false) }
    }

    fun startTimer() {
        _uiState.update { it.copy(isRunning = true, isPaused = false) }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.isRunning) {
                delay(1000)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            if (_uiState.value.remainingSeconds == 0) {
                _uiState.update { it.copy(isRunning = false, isCompleted = true) }
                _uiState.value.selectedOrb?.let { orb ->
                    focusRepository.insertFocusSession(
                        FocusSession(orbId = orb.id, durationMinutes = _uiState.value.durationMinutes)
                    )
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false, isPaused = true) }
    }

    fun resumeTimer() {
        if (!_uiState.value.isPaused) return
        startTimer()
    }

    fun stopTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                isRunning = false,
                isPaused = false,
                isCompleted = false,
                remainingSeconds = it.durationMinutes * 60
            )
        }
    }

    fun markOrbComplete() {
        _uiState.value.selectedOrb?.let {
            viewModelScope.launch {
                orbRepository.completeOrb(it.id)
                _uiState.update { s -> s.copy(isCompleted = false) }
                stopTimer()
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}

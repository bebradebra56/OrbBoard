package com.orbboard.boardoar.presentation.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbboard.boardoar.data.preferences.AppPreferences
import com.orbboard.boardoar.util.BackupManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val colorMode: String = "Neon",
    val boardStyle: String = "Grid",
    val notificationsEnabled: Boolean = true,
    val backupUri: Uri? = null,
    val isExporting: Boolean = false,
    val showExportSuccess: Boolean = false,
    val showExportError: Boolean = false
)

class SettingsViewModel(
    private val preferences: AppPreferences,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                preferences.colorMode,
                preferences.boardStyle,
                preferences.notificationsEnabled
            ) { colorMode, boardStyle, notifications ->
                Triple(colorMode, boardStyle, notifications)
            }.collect { (colorMode, boardStyle, notifications) ->
                _uiState.update {
                    it.copy(
                        colorMode = colorMode,
                        boardStyle = boardStyle,
                        notificationsEnabled = notifications
                    )
                }
            }
        }
    }

    fun setColorMode(mode: String) {
        viewModelScope.launch {
            preferences.setColorMode(mode)
        }
    }

    fun setBoardStyle(style: String) {
        viewModelScope.launch {
            preferences.setBoardStyle(style)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setNotificationsEnabled(enabled)
        }
    }

    fun exportBackup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true) }
            val uri = backupManager.exportDatabase()
            _uiState.update {
                it.copy(
                    isExporting = false,
                    backupUri = uri,
                    showExportSuccess = uri != null,
                    showExportError = uri == null
                )
            }
        }
    }

    fun consumeBackupUri(): Uri? {
        val uri = _uiState.value.backupUri
        _uiState.update { it.copy(backupUri = null, showExportSuccess = false) }
        return uri
    }

    fun dismissError() = _uiState.update { it.copy(showExportError = false) }
}

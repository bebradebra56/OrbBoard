package com.orbboard.boardoar.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "orb_prefs")

class AppPreferences(private val context: Context) {

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val COLOR_MODE = stringPreferencesKey("color_mode")
        val BOARD_STYLE = stringPreferencesKey("board_style")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { it[ONBOARDING_COMPLETED] ?: false }

    val colorMode: Flow<String> = context.dataStore.data
        .map { it[COLOR_MODE] ?: "Neon" }

    val boardStyle: Flow<String> = context.dataStore.data
        .map { it[BOARD_STYLE] ?: "Grid" }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { it[NOTIFICATIONS_ENABLED] ?: true }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setColorMode(mode: String) {
        context.dataStore.edit { it[COLOR_MODE] = mode }
    }

    suspend fun setBoardStyle(style: String) {
        context.dataStore.edit { it[BOARD_STYLE] = style }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }
}

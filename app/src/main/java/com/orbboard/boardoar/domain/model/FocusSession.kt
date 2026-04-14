package com.orbboard.boardoar.domain.model

data class FocusSession(
    val id: Long = 0,
    val orbId: Long,
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis()
)

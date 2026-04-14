package com.orbboard.boardoar.domain.model

data class SubTask(
    val id: Long = 0,
    val orbId: Long,
    val title: String,
    val isCompleted: Boolean = false
)

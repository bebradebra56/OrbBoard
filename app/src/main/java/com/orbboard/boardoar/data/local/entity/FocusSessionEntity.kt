package com.orbboard.boardoar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orbId: Long,
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis()
)

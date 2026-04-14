package com.orbboard.boardoar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orbs")
data class OrbEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val categoryId: Long,
    val dueDate: Long? = null,
    val priority: String = "MEDIUM",
    val size: String = "MEDIUM",
    val colorHex: String = "#7A5CFF",
    val posX: Float = 0.5f,
    val posY: Float = 0.5f,
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val tags: String = ""
)

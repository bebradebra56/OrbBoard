package com.orbboard.boardoar.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sub_tasks")
data class SubTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orbId: Long,
    val title: String,
    val isCompleted: Boolean = false
)

package com.orbboard.boardoar.data.local.database

import androidx.room.*
import com.orbboard.boardoar.data.local.dao.*
import com.orbboard.boardoar.data.local.entity.*

@Database(
    entities = [
        OrbEntity::class,
        CategoryEntity::class,
        SubTaskEntity::class,
        FocusSessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class OrbDatabase : RoomDatabase() {

    abstract fun orbDao(): OrbDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subTaskDao(): SubTaskDao
    abstract fun focusSessionDao(): FocusSessionDao

    companion object {
        const val DATABASE_NAME = "orb_board_db"
    }
}

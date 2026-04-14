package com.orbboard.boardoar.data.local.dao

import androidx.room.*
import com.orbboard.boardoar.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Insert
    suspend fun insertFocusSession(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions WHERE orbId = :orbId ORDER BY completedAt DESC")
    fun getFocusSessionsByOrb(orbId: Long): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions ORDER BY completedAt DESC")
    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT COUNT(*) FROM focus_sessions")
    fun getTotalSessionCount(): Flow<Int>
}

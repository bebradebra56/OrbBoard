package com.orbboard.boardoar.domain.repository

import com.orbboard.boardoar.domain.model.FocusSession
import com.orbboard.boardoar.domain.model.SubTask
import kotlinx.coroutines.flow.Flow

interface FocusRepository {
    fun getSubTasksByOrb(orbId: Long): Flow<List<SubTask>>
    suspend fun insertSubTask(subTask: SubTask): Long
    suspend fun updateSubTask(subTask: SubTask)
    suspend fun deleteSubTask(id: Long)
    suspend fun insertFocusSession(session: FocusSession)
    fun getFocusSessionsByOrb(orbId: Long): Flow<List<FocusSession>>
    fun getAllFocusSessions(): Flow<List<FocusSession>>
}

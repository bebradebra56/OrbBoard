package com.orbboard.boardoar.data.repository

import com.orbboard.boardoar.data.local.dao.FocusSessionDao
import com.orbboard.boardoar.data.local.dao.SubTaskDao
import com.orbboard.boardoar.data.local.entity.FocusSessionEntity
import com.orbboard.boardoar.data.local.entity.SubTaskEntity
import com.orbboard.boardoar.domain.model.FocusSession
import com.orbboard.boardoar.domain.model.SubTask
import com.orbboard.boardoar.domain.repository.FocusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FocusRepositoryImpl(
    private val subTaskDao: SubTaskDao,
    private val focusSessionDao: FocusSessionDao
) : FocusRepository {

    override fun getSubTasksByOrb(orbId: Long): Flow<List<SubTask>> =
        subTaskDao.getSubTasksByOrb(orbId).map { it.map(SubTaskEntity::toDomain) }

    override suspend fun insertSubTask(subTask: SubTask): Long =
        subTaskDao.insertSubTask(subTask.toEntity())

    override suspend fun updateSubTask(subTask: SubTask) =
        subTaskDao.updateSubTask(subTask.toEntity())

    override suspend fun deleteSubTask(id: Long) =
        subTaskDao.deleteSubTask(id)

    override suspend fun insertFocusSession(session: FocusSession) =
        focusSessionDao.insertFocusSession(session.toEntity())

    override fun getFocusSessionsByOrb(orbId: Long): Flow<List<FocusSession>> =
        focusSessionDao.getFocusSessionsByOrb(orbId).map { it.map(FocusSessionEntity::toDomain) }

    override fun getAllFocusSessions(): Flow<List<FocusSession>> =
        focusSessionDao.getAllFocusSessions().map { it.map(FocusSessionEntity::toDomain) }
}

private fun SubTaskEntity.toDomain() = SubTask(id, orbId, title, isCompleted)
private fun SubTask.toEntity() = SubTaskEntity(id, orbId, title, isCompleted)
private fun FocusSessionEntity.toDomain() = FocusSession(id, orbId, durationMinutes, completedAt)
private fun FocusSession.toEntity() = FocusSessionEntity(id, orbId, durationMinutes, completedAt)

package com.orbboard.boardoar.data.repository

import com.orbboard.boardoar.data.local.dao.OrbDao
import com.orbboard.boardoar.data.local.entity.OrbEntity
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.domain.model.OrbSize
import com.orbboard.boardoar.domain.model.Priority
import com.orbboard.boardoar.domain.repository.OrbRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrbRepositoryImpl(private val dao: OrbDao) : OrbRepository {

    override fun getAllOrbs(): Flow<List<Orb>> =
        dao.getAllOrbs().map { it.map(OrbEntity::toDomain) }

    override fun getOrbsByCategory(categoryId: Long): Flow<List<Orb>> =
        dao.getOrbsByCategory(categoryId).map { it.map(OrbEntity::toDomain) }

    override fun getCompletedOrbs(): Flow<List<Orb>> =
        dao.getCompletedOrbs().map { it.map(OrbEntity::toDomain) }

    override fun getArchivedOrbs(): Flow<List<Orb>> =
        dao.getArchivedOrbs().map { it.map(OrbEntity::toDomain) }

    override suspend fun getOrbById(id: Long): Orb? = dao.getOrbById(id)?.toDomain()

    override suspend fun insertOrb(orb: Orb): Long = dao.insertOrb(orb.toEntity())

    override suspend fun updateOrb(orb: Orb) = dao.updateOrb(orb.toEntity())

    override suspend fun updateOrbPosition(id: Long, posX: Float, posY: Float) =
        dao.updateOrbPosition(id, posX, posY)

    override suspend fun deleteOrb(id: Long) = dao.deleteOrb(id)

    override suspend fun completeOrb(id: Long) =
        dao.completeOrb(id, System.currentTimeMillis())

    override fun searchOrbs(query: String): Flow<List<Orb>> =
        dao.searchOrbs(query).map { it.map(OrbEntity::toDomain) }

    override fun getOrbsCompletedSince(weekStart: Long): Flow<List<Orb>> =
        dao.getOrbsCompletedSince(weekStart).map { it.map(OrbEntity::toDomain) }
}

private fun OrbEntity.toDomain() = Orb(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId,
    dueDate = dueDate,
    priority = runCatching { Priority.valueOf(priority) }.getOrDefault(Priority.MEDIUM),
    size = runCatching { OrbSize.valueOf(size) }.getOrDefault(OrbSize.MEDIUM),
    colorHex = colorHex,
    posX = posX,
    posY = posY,
    isCompleted = isCompleted,
    isArchived = isArchived,
    createdAt = createdAt,
    completedAt = completedAt,
    tags = if (tags.isBlank()) emptyList() else tags.split(",").filter { it.isNotBlank() }
)

private fun Orb.toEntity() = OrbEntity(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId,
    dueDate = dueDate,
    priority = priority.name,
    size = size.name,
    colorHex = colorHex,
    posX = posX,
    posY = posY,
    isCompleted = isCompleted,
    isArchived = isArchived,
    createdAt = createdAt,
    completedAt = completedAt,
    tags = tags.joinToString(",")
)

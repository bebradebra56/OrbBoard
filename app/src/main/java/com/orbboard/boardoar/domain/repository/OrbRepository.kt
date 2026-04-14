package com.orbboard.boardoar.domain.repository

import com.orbboard.boardoar.domain.model.Orb
import kotlinx.coroutines.flow.Flow

interface OrbRepository {
    fun getAllOrbs(): Flow<List<Orb>>
    fun getOrbsByCategory(categoryId: Long): Flow<List<Orb>>
    fun getCompletedOrbs(): Flow<List<Orb>>
    fun getArchivedOrbs(): Flow<List<Orb>>
    suspend fun getOrbById(id: Long): Orb?
    suspend fun insertOrb(orb: Orb): Long
    suspend fun updateOrb(orb: Orb)
    suspend fun updateOrbPosition(id: Long, posX: Float, posY: Float)
    suspend fun deleteOrb(id: Long)
    suspend fun completeOrb(id: Long)
    fun searchOrbs(query: String): Flow<List<Orb>>
    fun getOrbsCompletedSince(weekStart: Long): Flow<List<Orb>>
}

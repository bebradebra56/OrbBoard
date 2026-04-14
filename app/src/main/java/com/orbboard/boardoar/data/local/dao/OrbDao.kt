package com.orbboard.boardoar.data.local.dao

import androidx.room.*
import com.orbboard.boardoar.data.local.entity.OrbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrbDao {

    @Query("SELECT * FROM orbs WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllOrbs(): Flow<List<OrbEntity>>

    @Query("SELECT * FROM orbs WHERE categoryId = :categoryId AND isArchived = 0 ORDER BY createdAt DESC")
    fun getOrbsByCategory(categoryId: Long): Flow<List<OrbEntity>>

    @Query("SELECT * FROM orbs WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedOrbs(): Flow<List<OrbEntity>>

    @Query("SELECT * FROM orbs WHERE isArchived = 1 ORDER BY createdAt DESC")
    fun getArchivedOrbs(): Flow<List<OrbEntity>>

    @Query("SELECT * FROM orbs WHERE id = :id LIMIT 1")
    suspend fun getOrbById(id: Long): OrbEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrb(orb: OrbEntity): Long

    @Update
    suspend fun updateOrb(orb: OrbEntity)

    @Query("UPDATE orbs SET posX = :posX, posY = :posY WHERE id = :id")
    suspend fun updateOrbPosition(id: Long, posX: Float, posY: Float)

    @Query("UPDATE orbs SET isCompleted = 1, completedAt = :completedAt WHERE id = :id")
    suspend fun completeOrb(id: Long, completedAt: Long)

    @Query("DELETE FROM orbs WHERE id = :id")
    suspend fun deleteOrb(id: Long)

    @Query("SELECT * FROM orbs WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') AND isArchived = 0")
    fun searchOrbs(query: String): Flow<List<OrbEntity>>

    @Query("SELECT * FROM orbs WHERE isCompleted = 1 AND completedAt >= :since ORDER BY completedAt DESC")
    fun getOrbsCompletedSince(since: Long): Flow<List<OrbEntity>>

    @Query("SELECT COUNT(*) FROM orbs WHERE isCompleted = 0 AND isArchived = 0")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM orbs WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>
}

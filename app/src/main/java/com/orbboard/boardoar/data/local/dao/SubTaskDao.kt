package com.orbboard.boardoar.data.local.dao

import androidx.room.*
import com.orbboard.boardoar.data.local.entity.SubTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubTaskDao {

    @Query("SELECT * FROM sub_tasks WHERE orbId = :orbId ORDER BY id ASC")
    fun getSubTasksByOrb(orbId: Long): Flow<List<SubTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: SubTaskEntity): Long

    @Update
    suspend fun updateSubTask(subTask: SubTaskEntity)

    @Query("DELETE FROM sub_tasks WHERE id = :id")
    suspend fun deleteSubTask(id: Long)

    @Query("DELETE FROM sub_tasks WHERE orbId = :orbId")
    suspend fun deleteSubTasksByOrb(orbId: Long)
}

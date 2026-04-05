package com.zetta.dicodingevent.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zetta.dicodingevent.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: List<EventEntity>)

    @Query("SELECT * FROM events WHERE active = :active ORDER BY beginTime DESC LIMIT :limit")
    fun getAll(active: Int, limit: Int = 40): Flow<List<EventEntity>>

    @Query("SELECT EXISTS (SELECT id FROM events WHERE id = :id LIMIT 1)")
    suspend fun any(id: Int): Boolean

    @Query("DELETE FROM events WHERE isFavorite = 0 AND active = :active")
    suspend fun clearCache(active: Int)

    @Update
    suspend fun update(event: EventEntity)

    @Query("SELECT * FROM events WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavorites(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE isFavorite = 1")
    fun getFavoritesSync(): List<EventEntity>

    @Query("SELECT id FROM events WHERE isFavorite = 1")
    fun getFavoriteIds(): Flow<List<Int>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    fun get(id: Int): Flow<EventEntity?>

}
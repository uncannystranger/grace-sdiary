package com.grace.sdiary.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.grace.sdiary.data.local.db.entity.DiaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {

    @Query("SELECT * FROM diary ORDER BY date DESC")
    fun getAll(): Flow<List<DiaryEntity>>

    @Query("SELECT * FROM diary WHERE id = :id")
    suspend fun getById(id: Long): DiaryEntity?

    @Query("SELECT * FROM diary WHERE date = :date")
    suspend fun getByDate(date: Long): DiaryEntity?

    @Query("SELECT * FROM diary WHERE date BETWEEN :startOfMonth AND :endOfMonth")
    fun getByMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<DiaryEntity>>

    @Query("SELECT * FROM diary WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<DiaryEntity>>

    @Query("SELECT * FROM diary ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int = 7): Flow<List<DiaryEntity>>

    @Query("SELECT COUNT(*) FROM diary")
    fun getCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DiaryEntity): Long

    @Update
    suspend fun update(entity: DiaryEntity)

    @Delete
    suspend fun delete(entity: DiaryEntity)

    @Query("SELECT * FROM diary ORDER BY date DESC")
    suspend fun getAllSync(): List<DiaryEntity>
}

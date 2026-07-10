package com.grace.sdiary.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.grace.sdiary.data.local.db.entity.VocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {

    @Query("SELECT * FROM vocabulary")
    fun getAll(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE id = :id")
    suspend fun getById(id: Long): VocabularyEntity?

    @Query("SELECT * FROM vocabulary WHERE word LIKE '%' || :query || '%' OR definition LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE difficulty = :difficulty")
    fun getByDifficulty(difficulty: String): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE status = :status")
    fun getByStatus(status: String): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary ORDER BY createdAt DESC LIMIT :limit")
    fun getRecent(limit: Int = 10): Flow<List<VocabularyEntity>>

    @Query("SELECT COUNT(*) FROM vocabulary")
    fun getCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: VocabularyEntity): Long

    @Update
    suspend fun update(entity: VocabularyEntity)

    @Delete
    suspend fun delete(entity: VocabularyEntity)

    @Query("SELECT * FROM vocabulary ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): VocabularyEntity?

    @Query("SELECT * FROM vocabulary")
    suspend fun getAllSync(): List<VocabularyEntity>
}

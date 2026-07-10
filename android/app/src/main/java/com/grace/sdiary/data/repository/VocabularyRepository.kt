package com.grace.sdiary.data.repository

import com.grace.sdiary.data.local.db.dao.VocabularyDao
import com.grace.sdiary.data.local.db.entity.VocabularyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VocabularyRepository(private val dao: VocabularyDao) {

    fun getAll(): Flow<List<VocabularyEntity>> = dao.getAll()

    fun search(query: String): Flow<List<VocabularyEntity>> = dao.search(query)

    fun getByDifficulty(difficulty: String): Flow<List<VocabularyEntity>> = dao.getByDifficulty(difficulty)

    fun getByStatus(status: String): Flow<List<VocabularyEntity>> = dao.getByStatus(status)

    fun getRecent(limit: Int = 10): Flow<List<VocabularyEntity>> = dao.getRecent(limit)

    fun getCount(): Flow<Int> = dao.getCount()

    suspend fun getById(id: Long): VocabularyEntity? = withContext(Dispatchers.IO) {
        dao.getById(id)
    }

    suspend fun insert(entity: VocabularyEntity): Long = withContext(Dispatchers.IO) {
        dao.insert(entity)
    }

    suspend fun update(entity: VocabularyEntity) = withContext(Dispatchers.IO) {
        dao.update(entity)
    }

    suspend fun delete(entity: VocabularyEntity) = withContext(Dispatchers.IO) {
        dao.delete(entity)
    }

    suspend fun getRandomWord(): VocabularyEntity? = withContext(Dispatchers.IO) {
        dao.getRandomWord()
    }
}

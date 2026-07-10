package com.grace.sdiary.data.repository

import com.grace.sdiary.data.local.db.dao.DiaryDao
import com.grace.sdiary.data.local.db.entity.DiaryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DiaryRepository(private val dao: DiaryDao) {

    fun getAll(): Flow<List<DiaryEntity>> = dao.getAll()

    suspend fun getByDate(date: Long): DiaryEntity? = withContext(Dispatchers.IO) {
        dao.getByDate(date)
    }

    fun getByMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<DiaryEntity>> =
        dao.getByMonth(startOfMonth, endOfMonth)

    fun search(query: String): Flow<List<DiaryEntity>> = dao.search(query)

    fun getRecent(limit: Int = 7): Flow<List<DiaryEntity>> = dao.getRecent(limit)

    fun getCount(): Flow<Int> = dao.getCount()

    suspend fun getById(id: Long): DiaryEntity? = withContext(Dispatchers.IO) {
        dao.getById(id)
    }

    suspend fun insert(entity: DiaryEntity): Long = withContext(Dispatchers.IO) {
        dao.insert(entity)
    }

    suspend fun update(entity: DiaryEntity) = withContext(Dispatchers.IO) {
        dao.update(entity)
    }

    suspend fun delete(entity: DiaryEntity) = withContext(Dispatchers.IO) {
        dao.delete(entity)
    }
}

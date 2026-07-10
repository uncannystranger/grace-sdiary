package com.grace.sdiary.data.repository

import com.grace.sdiary.data.local.db.dao.PlannerDao
import com.grace.sdiary.data.local.db.entity.PlannerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlannerRepository(private val dao: PlannerDao) {

    fun getAll(): Flow<List<PlannerEntity>> = dao.getAll()

    fun getByDate(date: Long): Flow<List<PlannerEntity>> = dao.getByDate(date)

    fun getByDateRange(start: Long, end: Long): Flow<List<PlannerEntity>> =
        dao.getByDateRange(start, end)

    fun getToday(today: Long): Flow<List<PlannerEntity>> = dao.getToday(today)

    fun getOverdue(now: Long): Flow<List<PlannerEntity>> = dao.getOverdue(now)

    fun search(query: String): Flow<List<PlannerEntity>> = dao.search(query)

    suspend fun getById(id: Long): PlannerEntity? = withContext(Dispatchers.IO) {
        dao.getById(id)
    }

    suspend fun insert(entity: PlannerEntity): Long = withContext(Dispatchers.IO) {
        dao.insert(entity)
    }

    suspend fun update(entity: PlannerEntity) = withContext(Dispatchers.IO) {
        dao.update(entity)
    }

    suspend fun delete(entity: PlannerEntity) = withContext(Dispatchers.IO) {
        dao.delete(entity)
    }
}

package com.grace.sdiary.data.repository

import com.grace.sdiary.data.local.db.dao.CalendarEventDao
import com.grace.sdiary.data.local.db.entity.CalendarEventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CalendarEventRepository(private val dao: CalendarEventDao) {

    fun getAll(): Flow<List<CalendarEventEntity>> = dao.getAll()

    fun getByDate(date: Long): Flow<List<CalendarEventEntity>> = dao.getByDate(date)

    fun getByMonth(startDate: Long, endDate: Long): Flow<List<CalendarEventEntity>> =
        dao.getByMonth(startDate, endDate)

    suspend fun getById(id: Long): CalendarEventEntity? = withContext(Dispatchers.IO) {
        dao.getById(id)
    }

    suspend fun insert(entity: CalendarEventEntity): Long = withContext(Dispatchers.IO) {
        dao.insert(entity)
    }

    suspend fun update(entity: CalendarEventEntity) = withContext(Dispatchers.IO) {
        dao.update(entity)
    }

    suspend fun delete(entity: CalendarEventEntity) = withContext(Dispatchers.IO) {
        dao.delete(entity)
    }
}

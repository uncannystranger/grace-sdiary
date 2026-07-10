package com.grace.sdiary.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val date: Long,
    val startTime: String? = null,
    val endTime: String? = null,
    val color: String? = null,
    val category: String? = null,
    val isAllDay: Boolean = false,
    val createdAt: Long
)

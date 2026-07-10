package com.grace.sdiary.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val timeOfDay: String,
    val startTime: String? = null,
    val durationMinutes: Int? = null,
    val daysOfWeek: String? = null,
    val icon: String? = null,
    val sortOrder: Int = 0,
    val isEnabled: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)

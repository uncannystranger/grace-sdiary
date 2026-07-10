package com.grace.sdiary.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val type: String,
    val dateTime: Long,
    val endDate: Long? = null,
    val repeatInterval: String? = null,
    val repeatDays: String? = null,
    val isEnabled: Boolean = true,
    val isCompleted: Boolean = false,
    val hasSound: Boolean = true,
    val snoozedUntil: Long? = null,
    val notificationId: Int = 0,
    val deepLink: String? = null,
    val createdAt: Long
)

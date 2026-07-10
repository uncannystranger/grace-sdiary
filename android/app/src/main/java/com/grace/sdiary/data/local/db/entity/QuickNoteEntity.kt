package com.grace.sdiary.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quick_notes")
data class QuickNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val category: String? = null,
    val isPinned: Boolean = false,
    val colorHex: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

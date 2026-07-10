package com.grace.sdiary.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val pronunciation: String? = null,
    val partOfSpeech: String? = null,
    val definition: String,
    val example: String? = null,
    val synonyms: String? = null,
    val arabicTranslation: String? = null,
    val difficulty: String = "beginner",
    val status: String = "learning",
    val tags: String? = null,
    val createdAt: Long,
    val lastReviewedAt: Long? = null,
    val reviewCount: Int = 0
)

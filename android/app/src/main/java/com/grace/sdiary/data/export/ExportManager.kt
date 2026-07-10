package com.grace.sdiary.data.export

import android.content.Context
import android.net.Uri
import com.grace.sdiary.data.local.db.AppDatabase
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExportManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase
) {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun export(format: String): Uri = withContext(Dispatchers.IO) {
        when (format.lowercase()) {
            "json" -> exportJson()
            "csv" -> exportCsv()
            else -> throw IllegalArgumentException("Unsupported format: $format")
        }
    }

    private fun exportJson(): Uri {
        val data = mapOf(
            "vocabulary" to database.vocabularyDao().getAllSync(),
            "diary" to database.diaryDao().getAllSync(),
            "goals" to database.goalDao().getAllSync(),
            "habits" to database.habitDao().getAllSync(),
            "planner" to database.plannerDao().getAllSync(),
            "routines" to database.routineDao().getAllSync(),
            "reminders" to database.reminderDao().getAllSync(),
            "notes" to database.quickNoteDao().getAllSync()
        )
        val json = gson.toJson(data)
        val file = File(context.cacheDir, "grace_diary_export.json")
        file.writeText(json)
        Uri.fromFile(file)
    }

    private fun exportCsv(): Uri {
        val sb = StringBuilder()
        // Vocabulary
        sb.appendLine("=== VOCABULARY ===")
        sb.appendLine("Word,Definition,Part of Speech,Difficulty,Status")
        database.vocabularyDao().getAllSync().forEach {
            sb.appendLine("${it.word},${it.definition},${it.partOfSpeech ?: ""},${it.difficulty},${it.status}")
        }
        val file = File(context.cacheDir, "grace_diary_export.csv")
        file.writeText(sb.toString())
        return Uri.fromFile(file)
    }
}

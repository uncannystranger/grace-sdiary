package com.grace.sdiary.data.backup

import android.content.Context
import android.net.Uri
import com.grace.sdiary.data.local.db.AppDatabase
import com.grace.sdiary.data.local.datastore.UserPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val prefs: UserPreferences
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun createBackup(): Uri = withContext(Dispatchers.IO) {
        val backup = BackupData(
            version = 1,
            timestamp = System.currentTimeMillis(),
            vocabulary = database.vocabularyDao().getAllSync(),
            diary = database.diaryDao().getAllSync(),
            goals = database.goalDao().getAllSync(),
            habits = database.habitDao().getAllSync(),
            habitLogs = database.habitLogDao().getAllSync(),
            planner = database.plannerDao().getAllSync(),
            routines = database.routineDao().getAllSync(),
            reminders = database.reminderDao().getAllSync(),
            calendarEvents = database.calendarEventDao().getAllSync(),
            quickNotes = database.quickNoteDao().getAllSync(),
            preferences = BackupPreferences(
                dailyXp = prefs.dailyXp.first(),
                streakCount = prefs.streakCount.first(),
                theme = prefs.theme.first(),
                userName = prefs.userName.first()
            )
        )
        val json = gson.toJson(backup)
        val fileName = "grace_diary_backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.json"
        val file = File(context.cacheDir, fileName)
        file.writeText(json)
        Uri.fromFile(file)
    }

    suspend fun restoreBackup(uri: Uri) = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri) ?: throw IOException("Cannot open file")
        val json = inputStream.bufferedReader().use { it.readText() }
        val backup = gson.fromJson(json, BackupData::class.java)
        // Clear existing data
        database.clearAllTables()
        // Restore
        backup.vocabulary.forEach { database.vocabularyDao().insert(it) }
        backup.diary.forEach { database.diaryDao().insert(it) }
        backup.goals.forEach { database.goalDao().insert(it) }
        backup.habits.forEach { database.habitDao().insert(it) }
        backup.habitLogs.forEach { database.habitLogDao().insert(it) }
        backup.planner.forEach { database.plannerDao().insert(it) }
        backup.routines.forEach { database.routineDao().insert(it) }
        backup.reminders.forEach { database.reminderDao().insert(it) }
        backup.calendarEvents.forEach { database.calendarEventDao().insert(it) }
        backup.quickNotes.forEach { database.quickNoteDao().insert(it) }
        // Restore preferences
        prefs.setDailyXp(backup.preferences.dailyXp)
        prefs.setStreakCount(backup.preferences.streakCount)
        prefs.setTheme(backup.preferences.theme)
        prefs.setUserName(backup.preferences.userName)
    }
}

data class BackupData(
    val version: Int,
    val timestamp: Long,
    val vocabulary: List<com.grace.sdiary.data.local.db.entity.VocabularyEntity>,
    val diary: List<com.grace.sdiary.data.local.db.entity.DiaryEntity>,
    val goals: List<com.grace.sdiary.data.local.db.entity.GoalEntity>,
    val habits: List<com.grace.sdiary.data.local.db.entity.HabitEntity>,
    val habitLogs: List<com.grace.sdiary.data.local.db.entity.HabitLogEntity>,
    val planner: List<com.grace.sdiary.data.local.db.entity.PlannerEntity>,
    val routines: List<com.grace.sdiary.data.local.db.entity.RoutineEntity>,
    val reminders: List<com.grace.sdiary.data.local.db.entity.ReminderEntity>,
    val calendarEvents: List<com.grace.sdiary.data.local.db.entity.CalendarEventEntity>,
    val quickNotes: List<com.grace.sdiary.data.local.db.entity.QuickNoteEntity>,
    val preferences: BackupPreferences
)

data class BackupPreferences(
    val dailyXp: Int,
    val streakCount: Int,
    val theme: String,
    val userName: String
)

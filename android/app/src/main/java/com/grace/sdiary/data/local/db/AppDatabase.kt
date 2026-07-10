package com.grace.sdiary.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.grace.sdiary.data.local.db.dao.CalendarEventDao
import com.grace.sdiary.data.local.db.dao.DiaryDao
import com.grace.sdiary.data.local.db.dao.GoalDao
import com.grace.sdiary.data.local.db.dao.HabitDao
import com.grace.sdiary.data.local.db.dao.HabitLogDao
import com.grace.sdiary.data.local.db.dao.PlannerDao
import com.grace.sdiary.data.local.db.dao.QuickNoteDao
import com.grace.sdiary.data.local.db.dao.ReminderDao
import com.grace.sdiary.data.local.db.dao.RoutineDao
import com.grace.sdiary.data.local.db.dao.VocabularyDao
import com.grace.sdiary.data.local.db.entity.CalendarEventEntity
import com.grace.sdiary.data.local.db.entity.DiaryEntity
import com.grace.sdiary.data.local.db.entity.GoalEntity
import com.grace.sdiary.data.local.db.entity.HabitEntity
import com.grace.sdiary.data.local.db.entity.HabitLogEntity
import com.grace.sdiary.data.local.db.entity.PlannerEntity
import com.grace.sdiary.data.local.db.entity.QuickNoteEntity
import com.grace.sdiary.data.local.db.entity.ReminderEntity
import com.grace.sdiary.data.local.db.entity.RoutineEntity
import com.grace.sdiary.data.local.db.entity.VocabularyEntity

@Database(
    entities = [
        VocabularyEntity::class,
        DiaryEntity::class,
        GoalEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        PlannerEntity::class,
        RoutineEntity::class,
        ReminderEntity::class,
        CalendarEventEntity::class,
        QuickNoteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vocabularyDao(): VocabularyDao
    abstract fun diaryDao(): DiaryDao
    abstract fun goalDao(): GoalDao
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun plannerDao(): PlannerDao
    abstract fun routineDao(): RoutineDao
    abstract fun reminderDao(): ReminderDao
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun quickNoteDao(): QuickNoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sdiary_database"
                )
                    // TODO: Replace with proper migrations for production
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

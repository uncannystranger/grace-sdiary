package com.grace.sdiary.di

import android.content.Context
import androidx.room.Room
import com.grace.sdiary.data.local.db.AppDatabase
import com.grace.sdiary.data.local.db.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "grace_diary_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideVocabularyDao(db: AppDatabase): VocabularyDao = db.vocabularyDao()
    @Provides fun provideDiaryDao(db: AppDatabase): DiaryDao = db.diaryDao()
    @Provides fun provideGoalDao(db: AppDatabase): GoalDao = db.goalDao()
    @Provides fun provideHabitDao(db: AppDatabase): HabitDao = db.habitDao()
    @Provides fun provideHabitLogDao(db: AppDatabase): HabitLogDao = db.habitLogDao()
    @Provides fun providePlannerDao(db: AppDatabase): PlannerDao = db.plannerDao()
    @Provides fun provideRoutineDao(db: AppDatabase): RoutineDao = db.routineDao()
    @Provides fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()
    @Provides fun provideCalendarEventDao(db: AppDatabase): CalendarEventDao = db.calendarEventDao()
    @Provides fun provideQuickNoteDao(db: AppDatabase): QuickNoteDao = db.quickNoteDao()
}

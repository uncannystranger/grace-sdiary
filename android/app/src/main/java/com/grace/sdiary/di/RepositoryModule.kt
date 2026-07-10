package com.grace.sdiary.di

import com.grace.sdiary.data.local.db.dao.*
import com.grace.sdiary.data.local.datastore.UserPreferences
import com.grace.sdiary.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides @Singleton
    fun provideVocabularyRepository(dao: VocabularyDao): VocabularyRepository = VocabularyRepository(dao)
    @Provides @Singleton
    fun provideDiaryRepository(dao: DiaryDao): DiaryRepository = DiaryRepository(dao)
    @Provides @Singleton
    fun provideGoalRepository(dao: GoalDao): GoalRepository = GoalRepository(dao)
    @Provides @Singleton
    fun provideHabitRepository(dao: HabitDao, logDao: HabitLogDao): HabitRepository = HabitRepository(dao, logDao)
    @Provides @Singleton
    fun providePlannerRepository(dao: PlannerDao): PlannerRepository = PlannerRepository(dao)
    @Provides @Singleton
    fun provideRoutineRepository(dao: RoutineDao): RoutineRepository = RoutineRepository(dao)
    @Provides @Singleton
    fun provideReminderRepository(dao: ReminderDao): ReminderRepository = ReminderRepository(dao)
    @Provides @Singleton
    fun provideCalendarEventRepository(dao: CalendarEventDao): CalendarEventRepository = CalendarEventRepository(dao)
    @Provides @Singleton
    fun provideQuickNoteRepository(dao: QuickNoteDao): QuickNoteRepository = QuickNoteRepository(dao)
    @Provides @Singleton
    fun provideProgressRepository(plannerDao: PlannerDao, habitDao: HabitDao, habitLogDao: HabitLogDao, diaryDao: DiaryDao, vocabDao: VocabularyDao, prefs: UserPreferences): ProgressRepository = ProgressRepository(plannerDao, habitDao, habitLogDao, diaryDao, vocabDao, prefs)
}

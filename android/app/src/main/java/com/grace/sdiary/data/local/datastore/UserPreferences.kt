package com.grace.sdiary.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
        private val FONT_SCALE_KEY = floatPreferencesKey("font_scale")
        private val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        private val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        private val NAME_KEY = stringPreferencesKey("user_name")
        private val DAILY_XP_KEY = longPreferencesKey("daily_xp")
        private val STREAK_COUNT = intPreferencesKey("streak_count")
        private val LAST_ACTIVE_DATE = longPreferencesKey("last_active_date")
        private val WEEKLY_GOAL_MINUTES = intPreferencesKey("weekly_goal_minutes")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val LAST_BACKUP_DATE = longPreferencesKey("last_backup_date")
    }

    val theme: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_KEY] ?: "system"
    }.distinctUntilChanged()

    val fontScale: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[FONT_SCALE_KEY] ?: 1.0f
    }.distinctUntilChanged()

    val reducedMotion: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[REDUCED_MOTION] ?: false
    }.distinctUntilChanged()

    val highContrast: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[HIGH_CONTRAST] ?: false
    }.distinctUntilChanged()

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[NAME_KEY] ?: ""
    }.distinctUntilChanged()

    val dailyXp: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[DAILY_XP_KEY] ?: 0L
    }.distinctUntilChanged()

    val streakCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[STREAK_COUNT] ?: 0
    }.distinctUntilChanged()

    val lastActiveDate: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[LAST_ACTIVE_DATE] ?: 0L
    }.distinctUntilChanged()

    val weeklyGoalMinutes: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[WEEKLY_GOAL_MINUTES] ?: 300
    }.distinctUntilChanged()

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: "en"
    }.distinctUntilChanged()

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED] ?: true
    }.distinctUntilChanged()

    val lastBackupDate: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[LAST_BACKUP_DATE] ?: 0L
    }.distinctUntilChanged()

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { prefs -> prefs[THEME_KEY] = theme }
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { prefs -> prefs[FONT_SCALE_KEY] = scale }
    }

    suspend fun setReducedMotion(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[REDUCED_MOTION] = enabled }
    }

    suspend fun setHighContrast(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[HIGH_CONTRAST] = enabled }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs -> prefs[NAME_KEY] = name }
    }

    suspend fun setDailyXp(xp: Long) {
        context.dataStore.edit { prefs -> prefs[DAILY_XP_KEY] = xp }
    }

    suspend fun setStreakCount(count: Int) {
        context.dataStore.edit { prefs -> prefs[STREAK_COUNT] = count }
    }

    suspend fun setLastActiveDate(timestamp: Long) {
        context.dataStore.edit { prefs -> prefs[LAST_ACTIVE_DATE] = timestamp }
    }

    suspend fun setWeeklyGoalMinutes(minutes: Int) {
        context.dataStore.edit { prefs -> prefs[WEEKLY_GOAL_MINUTES] = minutes }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { prefs -> prefs[LANGUAGE_KEY] = language }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setLastBackupDate(timestamp: Long) {
        context.dataStore.edit { prefs -> prefs[LAST_BACKUP_DATE] = timestamp }
    }

    suspend fun getLastActiveDate(): Long {
        return context.dataStore.data.first()[LAST_ACTIVE_DATE] ?: 0L
    }
}

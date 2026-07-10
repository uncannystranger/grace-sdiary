package com.grace.sdiary.util

import androidx.compose.ui.graphics.Color

object Constants {
    const val APP_NAME = "Grace's Diary"
    const val PREFS_NAME = "grace_diary_prefs"
    const val NOTIFICATION_CHANNEL_REMINDERS = "reminders"
    const val NOTIFICATION_CHANNEL_STUDY = "study_reminders"
    const val NOTIFICATION_CHANNEL_WATER = "water_reminders"
    const val NOTIFICATION_CHANNEL_DIARY = "diary_reminders"
    const val NOTIFICATION_CHANNEL_GENERAL = "general_notifications"
    const val WORK_NAME_CHECK_REMINDERS = "check_reminders"
    const val WORK_NAME_BACKUP = "auto_backup"
    const val WORK_NAME_RESET_DAILY = "reset_daily"
    const val EXPORT_FOLDER = "GraceDiary"
    const val BACKUP_FILE = "grace_diary_backup.json"
    const val DAILY_XP_LIMIT = 100
    const val XP_PER_TASK = 10
    const val XP_PER_HABIT = 5
    const val XP_PER_VOCAB = 3
    const val XP_PER_DIARY = 15
    const val XP_STREAK_BONUS = 20
    val GOLD_COLOR = Color(0xFFE2A33D)
    val TEAL_COLOR = Color(0xFF2A7F8E)
    val FOREST_COLOR = Color(0xFF1B4332)
    val SAND_COLOR = Color(0xFFF2E9DC)
    val CORAL_COLOR = Color(0xFFff8fab)
}

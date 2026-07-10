package com.grace.sdiary.ui.navigation

sealed class Screen(val route: String, val title: String, val icon: String) {
    data object Dashboard : Screen("dashboard", "Dashboard", "layout-dashboard")
    data object Planner : Screen("planner", "Today's Planner", "clipboard-list")
    data object Calendar : Screen("calendar", "Calendar", "calendar-days")
    data object Routine : Screen("routine", "Routine", "sunrise")
    data object Habits : Screen("habits", "Habits", "flame")
    data object Vocabulary : Screen("vocabulary", "Vocabulary", "book-marked")
    data object EnglishNotes : Screen("english_notes", "English Notes", "languages")
    data object Diary : Screen("diary", "Diary", "notebook-pen")
    data object Reminders : Screen("reminders", "Reminders", "bell-ring")
    data object Goals : Screen("goals", "Goals", "target")
    data object Progress : Screen("progress", "Progress", "trending-up")
    data object Settings : Screen("settings", "Settings", "settings")
    data object Focus : Screen("focus", "Focus Mode", "clock")

    companion object {
        val all = listOf(Dashboard, Planner, Calendar, Routine, Habits, Vocabulary, EnglishNotes, Diary, Reminders, Goals, Progress, Settings, Focus)
    }
}

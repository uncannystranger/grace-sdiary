package com.grace.sdiary.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.grace.sdiary.ui.screens.calendar.CalendarScreen
import com.grace.sdiary.ui.screens.dashboard.DashboardScreen
import com.grace.sdiary.ui.screens.diary.DiaryScreen
import com.grace.sdiary.ui.screens.english.EnglishNotesScreen
import com.grace.sdiary.ui.screens.focus.FocusScreen
import com.grace.sdiary.ui.screens.goals.GoalsScreen
import com.grace.sdiary.ui.screens.habits.HabitsScreen
import com.grace.sdiary.ui.screens.planner.PlannerScreen
import com.grace.sdiary.ui.screens.progress.ProgressScreen
import com.grace.sdiary.ui.screens.reminders.RemindersScreen
import com.grace.sdiary.ui.screens.routine.RoutineScreen
import com.grace.sdiary.ui.screens.settings.SettingsScreen
import com.grace.sdiary.ui.screens.vocabulary.VocabularyScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Planner.route) {
            PlannerScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Calendar.route) {
            CalendarScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Routine.route) {
            RoutineScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Habits.route) {
            HabitsScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Vocabulary.route) {
            VocabularyScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.EnglishNotes.route) {
            EnglishNotesScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Diary.route) {
            DiaryScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Reminders.route) {
            RemindersScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Goals.route) {
            GoalsScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Progress.route) {
            ProgressScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onNavigate = { route -> navController.navigate(route) })
        }
        composable(Screen.Focus.route) {
            FocusScreen(onNavigate = { route -> navController.navigate(route) })
        }
    }
}

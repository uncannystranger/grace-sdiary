package com.grace.sdiary.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.grace.sdiary.ui.theme.Gold

private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val pageId: String
)

private val bottomNavItems = listOf(
    BottomNavItem("Dashboard", Icons.Default.Dashboard, "dashboard"),
    BottomNavItem("Planner", Icons.Default.DateRange, "planner"),
    BottomNavItem("Habits", Icons.Default.CheckCircle, "habits"),
    BottomNavItem("Diary", Icons.Default.Book, "diary"),
    BottomNavItem("Settings", Icons.Default.Settings, "settings")
)

@Composable
fun BottomNav(
    currentPage: String,
    onPageSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            val isActive = item.pageId == currentPage
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) },
                selected = isActive,
                onClick = { onPageSelected(item.pageId) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Gold,
                    selectedTextColor = Gold,
                    unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Gold.copy(alpha = 0.15f)
                )
            )
        }
    }
}

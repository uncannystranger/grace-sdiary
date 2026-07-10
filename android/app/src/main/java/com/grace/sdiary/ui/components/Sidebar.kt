package com.grace.sdiary.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grace.sdiary.ui.theme.AppTypography
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.Teal

private data class NavItem(
    val label: String,
    val icon: ImageVector,
    val pageId: String
)

private val navItems = listOf(
    NavItem("Dashboard", Icons.Default.Dashboard, "dashboard"),
    NavItem("Today's Planner", Icons.Default.DateRange, "planner"),
    NavItem("Calendar", Icons.Default.CalendarMonth, "calendar"),
    NavItem("Routine", Icons.Default.Repeat, "routine"),
    NavItem("Habits", Icons.Default.CheckCircle, "habits"),
    NavItem("Vocabulary", Icons.Default.MenuBook, "vocabulary"),
    NavItem("English Notes", Icons.Default.Article, "english"),
    NavItem("Diary", Icons.Default.AutoStories, "diary"),
    NavItem("Reminders", Icons.Default.Notifications, "reminders"),
    NavItem("Goals", Icons.Default.Flag, "goals"),
    NavItem("Progress", Icons.Default.TrendingUp, "progress"),
    NavItem("Settings", Icons.Default.Settings, "settings")
)

@Composable
fun Sidebar(
    currentPage: String,
    onPageSelected: (String) -> Unit,
    onClose: () -> Unit,
    isMobile: Boolean
) {
    val glass = GraceTheme.glassColors
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(glass.cardBackground)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = "Sihaam",
                    modifier = Modifier.size(28.dp),
                    tint = Teal
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Sihaam",
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = glass.textHigh
                )
                if (!isMobile) {
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Close sidebar",
                            tint = glass.textMid
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(
                        text = "Quick nav...",
                        style = AppTypography.bodySmall,
                        color = glass.textLow
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(18.dp),
                        tint = glass.textMid
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = glass.textHigh,
                    unfocusedTextColor = glass.textHigh,
                    focusedBorderColor = glass.glassBorder,
                    unfocusedBorderColor = glass.glassBorder,
                    cursorColor = Gold,
                    focusedContainerColor = glass.glassBackground,
                    unfocusedContainerColor = glass.glassBackground
                ),
                textStyle = AppTypography.bodySmall
            )

            Spacer(Modifier.height(12.dp))

            navItems.forEach { item ->
                val isActive = item.pageId == currentPage
                NavItemRow(
                    item = item,
                    isActive = isActive,
                    onClick = { onPageSelected(item.pageId) }
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = glass.glassBackground,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = Gold.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "S",
                            style = AppTypography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Sihaam",
                    style = AppTypography.bodyMedium,
                    color = glass.textHigh
                )
                Spacer(Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = glass.glassBackground
                ) {
                    Text(
                        text = "⌘K",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = AppTypography.labelSmall,
                        color = glass.textLow
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItemRow(
    item: NavItem,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val glass = GraceTheme.glassColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable(onClick = onClick)
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(Gold)
                    .align(Alignment.CenterStart)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (isActive) {
                        Brush.horizontalGradient(
                            colors = listOf(Gold.copy(alpha = 0.2f), Color.Transparent),
                            startX = 0f,
                            endX = Float.POSITIVE_INFINITY
                        )
                    } else {
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color.Transparent)
                        )
                    }
                )
                .padding(start = 20.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(20.dp),
                tint = if (isActive) Gold else glass.textMid
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = item.label,
                style = AppTypography.bodyMedium,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isActive) glass.textHigh else glass.textMid
            )
        }
    }
}

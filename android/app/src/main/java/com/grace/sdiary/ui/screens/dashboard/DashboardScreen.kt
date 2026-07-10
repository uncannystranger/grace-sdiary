package com.grace.sdiary.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.local.db.entity.PlannerEntity
import com.grace.sdiary.data.model.WeeklyProgress
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.components.RingProgress
import com.grace.sdiary.ui.components.StatRow
import com.grace.sdiary.ui.components.TimelineItem
import com.grace.sdiary.ui.theme.AppTypography
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.Success
import com.grace.sdiary.ui.theme.Teal
import com.grace.sdiary.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val glass = GraceTheme.glassColors
    val greeting by viewModel.greeting.collectAsState()
    val weeklyProgress by viewModel.weeklyProgress.collectAsState()
    val dailyStats by viewModel.dailyStats.collectAsState()
    val todaySchedule by viewModel.todaySchedule.collectAsState()
    val isTablet = LocalConfiguration.current.screenWidthDp >= 600
    val quickActionColumns = if (isTablet) 5 else 3

    Box(modifier = Modifier.fillMaxSize()) {
        LiquidBackground()

        Scaffold(
            containerColor = Color.Transparent,
            contentColor = glass.textHigh
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GreetingSection(greeting = greeting)

                DailyProgressSection(
                    weeklyProgress = weeklyProgress,
                    dailyStats = dailyStats
                )

                TodayScheduleSection(
                    schedule = todaySchedule,
                    onToggle = viewModel::completePlannerItem
                )

                QuickActionsSection(
                    columns = quickActionColumns,
                    onNavigate = onNavigate
                )

                TodayHabitsSection()

                XpLevelSection(weeklyProgress = weeklyProgress)

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GreetingSection(greeting: String) {
    val glass = GraceTheme.glassColors
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn()
    ) {
        Column {
            Text(
                text = greeting,
                style = AppTypography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = glass.textHigh
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = DateUtils.formatDate(System.currentTimeMillis(), "EEEE, MMMM d"),
                style = AppTypography.bodyMedium,
                color = glass.textMid
            )
        }
    }
}

@Composable
private fun DailyProgressSection(
    weeklyProgress: WeeklyProgress,
    dailyStats: com.grace.sdiary.data.model.DailyStats
) {
    val glass = GraceTheme.glassColors
    val progressPct = (weeklyProgress.levelProgress * 100f).coerceIn(0f, 100f)
    val progressLabel = "${weeklyProgress.levelProgress * 100}%"

    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Daily Progress",
                style = AppTypography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = glass.textHigh
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RingProgress(
                    percentage = progressPct,
                    label = if (weeklyProgress.levelProgress >= 1f) "Max" else progressLabel,
                    sublabel = "Progress",
                    size = 100.dp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatRow(
                        icon = Icons.Filled.Star,
                        label = "XP Today",
                        value = "${dailyStats.xp}"
                    )
                    StatRow(
                        icon = Icons.Filled.FlashOn,
                        label = "Streak",
                        value = "${weeklyProgress.streak} days"
                    )
                    StatRow(
                        icon = Icons.Filled.Today,
                        label = "Weekly Goal",
                        value = "${weeklyProgress.weeklyProgressMinutes}/${weeklyProgress.weeklyGoalMinutes}min"
                    )
                }
            }

            LevelBadge(
                level = weeklyProgress.level,
                title = weeklyProgress.levelTitle,
                progress = weeklyProgress.levelProgress
            )
        }
    }
}

@Composable
private fun LevelBadge(level: Int, title: String, progress: Float) {
    val glass = GraceTheme.glassColors
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "levelProgress"
    )

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(listOf(Gold, Teal))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lv.$level",
                    style = AppTypography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text = title,
                style = AppTypography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = glass.textHigh
            )
            Spacer(Modifier.weight(1f))
        }

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Gold,
            trackColor = glass.textLow.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun TodayScheduleSection(
    schedule: List<PlannerEntity>,
    onToggle: (Long) -> Unit
) {
    val glass = GraceTheme.glassColors
    val completedCount = schedule.count { it.isComplete }

    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = Teal,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Today's Schedule",
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = glass.textHigh
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "$completedCount/${schedule.size}",
                    style = AppTypography.labelLarge,
                    color = glass.textMid
                )
            }

            if (schedule.isEmpty()) {
                Text(
                    text = "No tasks scheduled for today",
                    style = AppTypography.bodyMedium,
                    color = glass.textLow,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                schedule.forEachIndexed { index, item ->
                    val timeStr = item.startTime?.let { DateUtils.formatTime(it) } ?: ""
                    TimelineItem(
                        time = timeStr,
                        title = item.title,
                        subtitle = item.description,
                        color = if (item.isComplete) Success else Teal,
                        isLast = index == schedule.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsSection(columns: Int, onNavigate: (String) -> Unit) {
    val glass = GraceTheme.glassColors

    data class Action(val label: String, val icon: ImageVector, val route: String)
    val actions = listOf(
        Action("Focus Mode", Icons.Filled.Timer, "focus"),
        Action("Add Habit", Icons.Filled.AddCircle, "habits"),
        Action("New Diary", Icons.Filled.EditNote, "diary"),
        Action("New Reminder", Icons.Filled.Notifications, "reminders"),
        Action("Study Vocab", Icons.Filled.School, "vocabulary")
    )

    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Quick Actions",
                style = AppTypography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = glass.textHigh
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                actions.chunked(columns).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { action ->
                            QuickActionButton(
                                icon = action.icon,
                                label = action.label,
                                modifier = Modifier.weight(1f),
                                onClick = { onNavigate(action.route) }
                            )
                        }
                        if (row.size < columns) {
                            Spacer(Modifier.weight((columns - row.size).toFloat()))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val glass = GraceTheme.glassColors
    var pressed by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = glass.glassBackground.copy(alpha = if (pressed) 0.3f else 0.15f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(glass.glassBorder, glass.glassBorder))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = Teal
            )
            Text(
                text = label,
                style = AppTypography.labelSmall,
                color = glass.textMid,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TodayHabitsSection() {
    val glass = GraceTheme.glassColors

    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = Teal,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Today's Habits",
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = glass.textHigh
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "View all",
                    style = AppTypography.labelSmall,
                    color = Teal
                )
            }

            Text(
                text = "Your daily habits will appear here. Start building your routine!",
                style = AppTypography.bodySmall,
                color = glass.textLow
            )
        }
    }
}

@Composable
private fun XpLevelSection(weeklyProgress: WeeklyProgress) {
    val glass = GraceTheme.glassColors
    val animatedProgress by animateFloatAsState(
        targetValue = weeklyProgress.levelProgress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "xpLevelBar"
    )

    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "${weeklyProgress.xpTd} XP",
                        style = AppTypography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = glass.textHigh
                    )
                    Text(
                        text = "Level ${weeklyProgress.level} - ${weeklyProgress.levelTitle}",
                        style = AppTypography.bodySmall,
                        color = glass.textMid
                    )
                }
            }

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = Gold,
                trackColor = glass.textLow.copy(alpha = 0.15f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Level ${weeklyProgress.level}",
                    style = AppTypography.labelSmall,
                    color = glass.textLow
                )
                Text(
                    text = "Level ${weeklyProgress.level + 1}",
                    style = AppTypography.labelSmall,
                    color = glass.textLow
                )
            }
        }
    }
}

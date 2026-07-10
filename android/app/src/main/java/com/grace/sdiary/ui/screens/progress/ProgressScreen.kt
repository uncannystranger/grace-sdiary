package com.grace.sdiary.ui.screens.progress

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.model.LevelInfo
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.*
import java.text.DecimalFormat
import kotlin.math.roundToInt

@Composable
fun ProgressScreen(
    onNavigate: (String) -> Unit,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val levelInfo by viewModel.levelInfo.collectAsState()
    val weeklyProgress by viewModel.weeklyProgress.collectAsState()
    val totalHabits by viewModel.totalHabits.collectAsState()
    val diaryCount by viewModel.diaryCount.collectAsState()
    val vocabCount by viewModel.vocabCount.collectAsState()
    val weeklyCompletion by viewModel.weeklyCompletion.collectAsState()

    val xpValue by viewModel.xp.collectAsState(initial = 0)
    val streakCount by viewModel.streak.collectAsState(initial = 0)

    val totalTasksThisWeek = weeklyProgress.size
    val completedTasksThisWeek = weeklyProgress.count { it.isComplete }

    Box(modifier = Modifier.fillMaxSize()) {
        LiquidBackground()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { Header() }
            item { LevelCard(levelInfo) }
            item { StatsGrid(xpValue, streakCount, completedTasksThisWeek, totalHabits.size, diaryCount, vocabCount) }
            item { WeeklyChart(weeklyProgress) }
            item { LevelTimeline(levelInfo) }
            item { XpBreakdown(xpValue) }
        }
    }
}

@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Progress & Analytics",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.BarChart,
            contentDescription = null,
            tint = Gold,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun LevelCard(levelInfo: LevelInfo) {
    val animatedXpProgress = remember { Animatable(0f) }

    LaunchedEffect(levelInfo) {
        animatedXpProgress.animateTo(
            levelInfo.progress,
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }

    GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Level ${levelInfo.level}",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Gold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = levelInfo.title,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { animatedXpProgress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    brush = Brush.horizontalGradient(listOf(Gold, Teal)),
                    trackColor = Color.White.copy(alpha = 0.12f),
                    strokeCap = StrokeCap.Round
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${levelInfo.currentXp} / ${levelInfo.xpToNext} XP to next level",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun StatsGrid(
    totalXp: Int,
    streak: Int,
    tasksCompleted: Int,
    habitsTracked: Int,
    diaryEntries: Int,
    vocabWords: Int
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Statistics",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(icon = Icons.Default.Star, value = "$totalXp", label = "Total XP", modifier = Modifier.weight(1f))
            StatCard(icon = Icons.Default.LocalFireDepartment, value = "$streak", label = "Day Streak", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(icon = Icons.Default.TaskAlt, value = "$tasksCompleted", label = "Tasks Done", modifier = Modifier.weight(1f))
            StatCard(icon = Icons.Default.Favorite, value = "$habitsTracked", label = "Habits", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(icon = Icons.Default.EditNote, value = "$diaryEntries", label = "Diary Entries", modifier = Modifier.weight(1f))
            StatCard(icon = Icons.Default.MenuBook, value = "$vocabWords", label = "Vocabulary", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeeklyChart(
    weeklyProgress: List<com.grace.sdiary.data.local.db.entity.PlannerEntity>
) {
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val daysData = remember(weeklyProgress) {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
        dayLabels.mapIndexed { index, _ ->
            val dateStart = cal.timeInMillis
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
            val dateEnd = cal.timeInMillis
            val count = weeklyProgress.count {
                it.date in dateStart until dateEnd && it.isComplete
            }
            cal.add(java.util.Calendar.DAY_OF_MONTH, -1)
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
            count
        }
    }
    val maxVal = daysData.maxOrNull()?.coerceAtLeast(1) ?: 1

    GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "This Week",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            val animatedHeights = daysData.map { remember { Animatable(0f) } }
            LaunchedEffect(daysData) {
                animatedHeights.forEachIndexed { i, anim ->
                    anim.animateTo(
                        if (maxVal > 0) daysData[i].toFloat() / maxVal else 0f,
                        animationSpec = tween(600, delayMillis = i * 80, easing = FastOutSlowInEasing)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                daysData.forEachIndexed { index, count ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$count",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height((120f * animatedHeights[index].value).dp.coerceAtLeast(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
                                brush = Brush.verticalGradient(listOf(Gold, Teal))
                            ) {}
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = dayLabels[index],
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LevelTimeline(currentLevelInfo: LevelInfo) {
    val levels = listOf(
        1 to "Beginner Diarist",
        3 to "Consistent Writer",
        5 to "Habit Seeker",
        8 to "Disciplined Mind",
        12 to "Growth Master",
        16 to "Wisdom Keeper",
        20 to "Legendary Scribe"
    )

    GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Level Milestones",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            levels.forEach { (level, title) ->
                val isUnlocked = currentLevelInfo.level >= level
                val isCurrent = currentLevelInfo.level == level
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(24.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isUnlocked) Teal else Color.White.copy(alpha = 0.1f),
                            border = if (isCurrent) BorderStroke(2.dp, Gold) else null
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "$level",
                                    color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.4f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.35f),
                        fontSize = 14.sp,
                        fontWeight = if (isUnlocked) FontWeight.Medium else FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (isCurrent) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Gold.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = "YOU",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = Gold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun XpBreakdown(totalXp: Int) {
    val categories = listOf(
        "Tasks" to 40f,
        "Habits" to 25f,
        "Diary" to 20f,
        "Vocab" to 15f
    )
    val colors = listOf(Gold, Teal, Color(0xFF7C4DFF), Color(0xFFFF7043))
    val total = categories.sumOf { (_, pct) -> pct }

    GlassCard(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "XP Breakdown",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    var startAngle = -90f
                    categories.forEachIndexed { index, (_, pct) ->
                        val sweep = (pct / total) * 360f
                        drawArc(
                            color = colors[index % colors.size],
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = true,
                            style = Stroke(width = 28.dp.toPx(), cap = StrokeCap.Butt)
                        )
                        startAngle += sweep
                    }
                }
                Text(
                    text = "${totalXp}XP",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            categories.forEachIndexed { index, (name, pct) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(10.dp),
                        shape = RoundedCornerShape(5.dp),
                        color = colors[index % colors.size]
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = name,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${pct.toInt()}%",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

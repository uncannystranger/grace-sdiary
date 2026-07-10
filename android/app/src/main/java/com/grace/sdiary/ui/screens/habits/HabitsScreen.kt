package com.grace.sdiary.ui.screens.habits

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fire
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.local.db.entity.HabitEntity
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.AppTypography
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.Success
import com.grace.sdiary.ui.theme.Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    onNavigate: (String) -> Unit,
    viewModel: HabitsViewModel = hiltViewModel()
) {
    val glass = GraceTheme.glassColors
    val habits by viewModel.allHabits.collectAsState()
    val todayCompleted by viewModel.todayCompleted.collectAsState()
    val streakData by viewModel.streakCount.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(habits) {
        viewModel.refreshTodayStatus(habits)
    }

    val completedCount = habits.count { todayCompleted[it.id] == true }
    val totalStreak = streakData.sumOf { it.currentStreak }

    Box(modifier = Modifier.fillMaxSize()) {
        LiquidBackground()

        Scaffold(
            containerColor = Color.Transparent,
            contentColor = glass.textHigh,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Teal,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Habit")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HeaderSection(
                    habitCount = habits.size,
                    streakCount = totalStreak,
                    glass = glass
                )

                TodayProgressSection(
                    completed = completedCount,
                    total = habits.size,
                    glass = glass
                )

                if (habits.isEmpty()) {
                    EmptyHabitsState(glass = glass)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(
                            items = habits,
                            key = { it.id }
                        ) { habit ->
                            HabitCard(
                                habit = habit,
                                isCompleted = todayCompleted[habit.id] == true,
                                onToggle = { viewModel.toggleHabit(habit.id) },
                                onDelete = { viewModel.deleteHabit(habit) },
                                glass = glass
                            )
                        }
                    }
                }

                Spacer(Modifier.height(80.dp))
            }
        }
    }

    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, desc, freq ->
                viewModel.addHabit(name, desc.ifBlank { null }, freq)
                showAddDialog = false
            },
            glass = glass
        )
    }
}

@Composable
private fun HeaderSection(
    habitCount: Int,
    streakCount: Int,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Habits",
                style = AppTypography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = glass.textHigh
            )
            Text(
                text = "$habitCount habits tracked",
                style = AppTypography.bodyMedium,
                color = glass.textMid
            )
        }
        GlassCard(modifier = Modifier.wrapContentWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    Text(
                        text = "$streakCount",
                        style = AppTypography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = glass.textHigh
                    )
                    Text(
                        text = "day streak",
                        style = AppTypography.labelSmall,
                        color = glass.textMid
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayProgressSection(
    completed: Int,
    total: Int,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    GlassCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (completed == total && total > 0) Success.copy(alpha = 0.2f) else Teal.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (completed == total && total > 0) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                    contentDescription = null,
                    tint = if (completed == total && total > 0) Success else Teal,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Today's Progress",
                    style = AppTypography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = glass.textHigh
                )
                val pct = if (total > 0) (completed * 100f / total) else 0f
                LinearProgressIndicator(
                    progress = { pct / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (completed == total) Success else Teal,
                    trackColor = glass.textLow.copy(alpha = 0.15f)
                )
            }
            Text(
                text = "$completed/$total",
                style = AppTypography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (completed == total) Success else glass.textHigh
            )
        }
    }
}

@Composable
private fun HabitCard(
    habit: HabitEntity,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    val animatedScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0.95f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "habitScale"
    )

    GlassCard(
        modifier = Modifier.animateItemPlacement(
            animationSpec = spring(Spring.DampingRatioMediumBouncy)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) Success.copy(alpha = 0.2f)
                        else Teal.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.MenuBook,
                    contentDescription = null,
                    tint = if (isCompleted) Success else Teal,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = habit.name,
                style = AppTypography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = glass.textHigh,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            if (habit.description != null) {
                Text(
                    text = habit.description,
                    style = AppTypography.labelSmall,
                    color = glass.textLow,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Fire,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${habit.currentStreak}",
                    style = AppTypography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
            }

            Spacer(Modifier.height(2.dp))

            FilledIconButton(
                onClick = onToggle,
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isCompleted) Success else glass.textLow.copy(alpha = 0.2f)
                )
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircleOutline,
                    contentDescription = if (isCompleted) "Completed" else "Mark complete",
                    tint = if (isCompleted) Color.White else glass.textMid,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyHabitsState(
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = glass.textLow.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "No habits yet!",
                style = AppTypography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = glass.textMid
            )
            Text(
                text = "Create your first habit",
                style = AppTypography.bodySmall,
                color = glass.textLow
            )
        }
    }
}

@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("daily") }
    val frequencies = listOf("daily", "weekly", "monthly")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = com.grace.sdiary.ui.theme.DarkCard,
        titleContentColor = glass.textHigh,
        textContentColor = glass.textMid,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("New Habit", style = AppTypography.headlineMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Habit name") },
                    placeholder = { Text("e.g. Read 30 minutes") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = glass.glassBorder,
                        focusedContainerColor = glass.glassBackground,
                        unfocusedContainerColor = glass.glassBackground,
                        cursorColor = Teal,
                        focusedTextColor = glass.textHigh,
                        unfocusedTextColor = glass.textHigh,
                        focusedLabelColor = Teal,
                        unfocusedLabelColor = glass.textMid
                    )
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    placeholder = { Text("Brief description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = glass.glassBorder,
                        focusedContainerColor = glass.glassBackground,
                        unfocusedContainerColor = glass.glassBackground,
                        cursorColor = Teal,
                        focusedTextColor = glass.textHigh,
                        unfocusedTextColor = glass.textHigh,
                        focusedLabelColor = Teal,
                        unfocusedLabelColor = glass.textMid
                    )
                )

                Text(
                    text = "Frequency",
                    style = AppTypography.labelLarge,
                    color = glass.textMid
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    frequencies.forEach { freq ->
                        FilterChip(
                            selected = frequency == freq,
                            onClick = { frequency = freq },
                            label = {
                                Text(
                                    text = freq.replaceFirstChar { it.uppercase() },
                                    style = AppTypography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Teal,
                                selectedLabelColor = Color.White,
                                containerColor = glass.glassBackground,
                                labelColor = glass.textMid
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = glass.glassBorder,
                                selectedBorderColor = Teal,
                                enabled = true,
                                selected = frequency == freq
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.trim(), description.trim(), frequency) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = glass.textMid)
            }
        }
    )
}

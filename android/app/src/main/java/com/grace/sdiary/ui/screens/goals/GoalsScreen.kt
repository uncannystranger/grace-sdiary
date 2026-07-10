package com.grace.sdiary.ui.screens.goals

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.local.db.entity.GoalEntity
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.*
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onNavigate: (String) -> Unit,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val weeklyGoals by viewModel.weeklyGoals.collectAsState()
    val monthlyGoals by viewModel.monthlyGoals.collectAsState()
    val yearlyGoals by viewModel.yearlyGoals.collectAsState()
    val allGoals by viewModel.allGoals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var expandedSection by remember { mutableStateOf("weekly") }

    val totalCompleted = allGoals.count { it.isCompleted }
    val totalActive = allGoals.size

    Box(modifier = Modifier.fillMaxSize()) {
        LiquidBackground()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                GoalsHeader(totalCompleted, totalActive)
            }
            item {
                GoalSection(
                    title = "Weekly Goals",
                    key = "weekly",
                    goals = weeklyGoals,
                    isExpanded = expandedSection == "weekly",
                    onToggle = { expandedSection = if (expandedSection == "weekly") "" else "weekly" },
                    onUpdateProgress = { id, v -> viewModel.updateProgress(id, v) },
                    onArchive = { viewModel.archiveGoal(it) },
                    onDelete = { viewModel.deleteGoal(it) }
                )
            }
            item {
                GoalSection(
                    title = "Monthly Goals",
                    key = "monthly",
                    goals = monthlyGoals,
                    isExpanded = expandedSection == "monthly",
                    onToggle = { expandedSection = if (expandedSection == "monthly") "" else "monthly" },
                    onUpdateProgress = { id, v -> viewModel.updateProgress(id, v) },
                    onArchive = { viewModel.archiveGoal(it) },
                    onDelete = { viewModel.deleteGoal(it) }
                )
            }
            item {
                GoalSection(
                    title = "Yearly Goals",
                    key = "yearly",
                    goals = yearlyGoals,
                    isExpanded = expandedSection == "yearly",
                    onToggle = { expandedSection = if (expandedSection == "yearly") "" else "yearly" },
                    onUpdateProgress = { id, v -> viewModel.updateProgress(id, v) },
                    onArchive = { viewModel.archiveGoal(it) },
                    onDelete = { viewModel.deleteGoal(it) }
                )
            }
        }
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = Teal,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Goal")
        }
    }
    if (showAddDialog) {
        AddGoalDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, type, target, unit ->
                viewModel.addGoal(title, type, target, unit)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun GoalsHeader(completed: Int, total: Int) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Goals",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    progress = { if (total == 0) 0f else completed.toFloat() / total },
                    modifier = Modifier.size(56.dp),
                    color = Teal,
                    trackColor = Color.White.copy(alpha = 0.15f),
                    strokeWidth = 5.dp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "$completed / $total",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "goals completed",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalSection(
    title: String,
    key: String,
    goals: List<GoalEntity>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onUpdateProgress: (Long, Double) -> Unit,
    onArchive: (Long) -> Unit,
    onDelete: (GoalEntity) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onToggle
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${goals.size}",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f)
                )
            }
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Spacer(modifier = Modifier.height(4.dp))
                if (goals.isEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No $title yet",
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                    }
                } else {
                    goals.forEach { goal ->
                        GoalCard(
                            goal = goal,
                            onUpdateProgress = { newVal -> onUpdateProgress(goal.id, newVal) },
                            onArchive = { onArchive(goal.id) },
                            onDelete = { onDelete(goal) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalCard(
    goal: GoalEntity,
    onUpdateProgress: (Double) -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit
) {
    val animatedProgress = remember { Animatable(0f) }
    val targetProgress = if (goal.targetValue > 0) (goal.currentValue / goal.targetValue).toFloat().coerceIn(0f, 1f) else 0f
    val decimalFormat = remember { DecimalFormat("#.#") }

    LaunchedEffect(targetProgress) {
        animatedProgress.animateTo(targetProgress, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 5.dp.toPx()
                    val arcSize = size - Offset(strokeWidth, strokeWidth) * 2
                    drawArc(
                        color = Color.White.copy(alpha = 0.15f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(strokeWidth, strokeWidth),
                        size = Size(arcSize.width, arcSize.height),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Teal, Gold),
                            center = center
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress.value,
                        useCenter = false,
                        topLeft = Offset(strokeWidth, strokeWidth),
                        size = Size(arcSize.width, arcSize.height),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "${(animatedProgress.value * 100).toInt()}%",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${decimalFormat.format(goal.currentValue)} / ${decimalFormat.format(goal.targetValue)}",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                    if (goal.unit != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = goal.unit,
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = Teal,
                    trackColor = Color.White.copy(alpha = 0.15f),
                    strokeCap = StrokeCap.Round
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (!goal.isCompleted) {
                    IconButton(onClick = { onUpdateProgress(goal.targetValue) }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.CheckCircleOutline,
                            contentDescription = "Complete",
                            tint = Teal,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = Teal,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onArchive, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Archive,
                        contentDescription = "Archive",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Red.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddGoalDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, Double, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("weekly") }
    var targetValue by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    val types = listOf("weekly", "monthly", "yearly")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("New Goal", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                    colors = textFieldColors()
                )
                Text("Type", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Teal.copy(alpha = 0.2f),
                                containerColor = Color.White.copy(alpha = 0.05f)
                            )
                        )
                    }
                }
                OutlinedTextField(
                    value = targetValue,
                    onValueChange = { targetValue = it },
                    label = { Text("Target Value") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit (e.g., hours, books)") },
                    singleLine = true,
                    colors = textFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetValue.toDoubleOrNull() ?: return@Button
                    onAdd(title, selectedType, target, unit.ifBlank { null })
                },
                enabled = title.isNotBlank() && targetValue.toDoubleOrNull() != null && (targetValue.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(alpha = 0.6f))
            }
        }
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White.copy(alpha = 0.8f),
    focusedBorderColor = Teal,
    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
    cursorColor = Teal,
    focusedLabelColor = Teal,
    unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
)

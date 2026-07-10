package com.grace.sdiary.ui.screens.routine

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.local.db.entity.RoutineEntity
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.AppTypography
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.Teal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineScreen(
    onNavigate: (String) -> Unit,
    viewModel: RoutineViewModel = hiltViewModel()
) {
    val glass = GraceTheme.glassColors
    val morningRoutines by viewModel.morningRoutines.collectAsState()
    val afternoonRoutines by viewModel.afternoonRoutines.collectAsState()
    val eveningRoutines by viewModel.eveningRoutines.collectAsState()
    val nightRoutines by viewModel.nightRoutines.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

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
                    Icon(Icons.Filled.Add, contentDescription = "Add Routine")
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                item {
                    HeaderSection(glass = glass)
                }

                item {
                    RoutineSection(
                        title = "Morning",
                        emoji = "\uD83C\uDF05",
                        routines = morningRoutines,
                        onToggle = viewModel::toggleEnabled,
                        onDelete = viewModel::deleteRoutine,
                        glass = glass
                    )
                }

                item {
                    RoutineSection(
                        title = "Afternoon",
                        emoji = "\u2600\uFE0F",
                        routines = afternoonRoutines,
                        onToggle = viewModel::toggleEnabled,
                        onDelete = viewModel::deleteRoutine,
                        glass = glass
                    )
                }

                item {
                    RoutineSection(
                        title = "Evening",
                        emoji = "\uD83C\uDF06",
                        routines = eveningRoutines,
                        onToggle = viewModel::toggleEnabled,
                        onDelete = viewModel::deleteRoutine,
                        glass = glass
                    )
                }

                item {
                    RoutineSection(
                        title = "Night",
                        emoji = "\uD83C\uDF19",
                        routines = nightRoutines,
                        onToggle = viewModel::toggleEnabled,
                        onDelete = viewModel::deleteRoutine,
                        glass = glass
                    )
                }

                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AddRoutineDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, timeOfDay ->
                viewModel.addRoutine(title = title, timeOfDay = timeOfDay)
                showAddDialog = false
            },
            glass = glass
        )
    }
}

@Composable
private fun HeaderSection(
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it / 2 }) + fadeIn()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.WbSunny,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = "Daily Routine",
                    style = AppTypography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = glass.textHigh
                )
                Text(
                    text = "Organize your day",
                    style = AppTypography.bodyMedium,
                    color = glass.textMid
                )
            }
        }
    }
}

@Composable
private fun RoutineSection(
    title: String,
    emoji: String,
    routines: List<RoutineEntity>,
    onToggle: (RoutineEntity) -> Unit,
    onDelete: (RoutineEntity) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    var expanded by remember { mutableStateOf(true) }
    var animated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { animated = true }

    GlassCard(
        modifier = Modifier.animateContentSize(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { expanded = !expanded }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = emoji,
                    fontSize = AppTypography.titleLarge.fontSize,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = title,
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = glass.textHigh,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${routines.size}",
                    style = AppTypography.labelLarge,
                    color = glass.textMid,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (expanded) 90f else 0f),
                    tint = glass.textMid
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = spring(Spring.DampingRatioMediumBouncy)
                )
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (routines.isEmpty()) {
                        Text(
                            text = "No $title routines yet",
                            style = AppTypography.bodyMedium,
                            color = glass.textLow,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        routines.forEachIndexed { index, routine ->
                            val animDelay = index * 50
                            RoutineItem(
                                routine = routine,
                                onToggle = { onToggle(routine) },
                                onDelete = { onDelete(routine) },
                                glass = glass
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineItem(
    routine: RoutineEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(glass.glassBackground)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.DragHandle,
            contentDescription = "Drag handle",
            tint = glass.textLow,
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = routine.title,
                style = AppTypography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (routine.isEnabled) glass.textHigh else glass.textLow
            )

            if (!routine.description.isNullOrBlank()) {
                Text(
                    text = routine.description,
                    style = AppTypography.bodySmall,
                    color = glass.textLow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (routine.startTime != null) {
                Text(
                    text = routine.startTime,
                    style = AppTypography.labelSmall,
                    color = Teal
                )
            }
        }

        Switch(
            checked = routine.isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Teal,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = glass.textLow.copy(alpha = 0.3f)
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete routine",
                tint = glass.textLow,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun AddRoutineDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    var title by remember { mutableStateOf("") }
    var timeOfDay by remember { mutableStateOf("morning") }
    val timeOptions = listOf("morning", "afternoon", "evening", "night")
    val timeEmojis = mapOf("morning" to "\uD83C\uDF05", "afternoon" to "\u2600\uFE0F", "evening" to "\uD83C\uDF06", "night" to "\uD83C\uDF19")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = com.grace.sdiary.ui.theme.DarkCard,
        titleContentColor = glass.textHigh,
        textContentColor = glass.textMid,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "New Routine",
                style = AppTypography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Routine title") },
                    placeholder = { Text("e.g. Morning meditation") },
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
                    text = "Time of Day",
                    style = AppTypography.labelLarge,
                    color = glass.textMid
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    timeOptions.forEach { option ->
                        FilterChip(
                            selected = timeOfDay == option,
                            onClick = { timeOfDay = option },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${timeEmojis[option]} ",
                                        style = AppTypography.labelMedium
                                    )
                                    Text(
                                        text = option.replaceFirstChar { it.uppercase() },
                                        style = AppTypography.labelMedium
                                    )
                                }
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
                                selected = timeOfDay == option
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title.trim(), timeOfDay) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add Routine", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = glass.textMid)
            }
        }
    )
}



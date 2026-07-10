package com.grace.sdiary.ui.screens.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.local.db.entity.CalendarEventEntity
import com.grace.sdiary.data.local.db.entity.PlannerEntity
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.AppTypography
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.Teal
import com.grace.sdiary.util.DateUtils
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigate: (String) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val glass = GraceTheme.glassColors
    val currentMonth by viewModel.currentMonth.collectAsState()
    val monthEvents by viewModel.monthEvents.collectAsState()
    val monthPlannerItems by viewModel.monthPlannerItems.collectAsState()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showAddDialog by remember { mutableStateOf(false) }
    val today = LocalDate.now()

    val selectedEvents = monthEvents.filter { DateUtils.isSameDay(it.date, selectedDate) }
    val selectedPlanner = monthPlannerItems.filter { DateUtils.isSameDay(it.date, selectedDate) }

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
                    Icon(Icons.Filled.Add, contentDescription = "Add Event")
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
                    MonthNavigationHeader(
                        currentMonth = currentMonth,
                        onPrevious = viewModel::previousMonth,
                        onNext = viewModel::nextMonth,
                        onSwipe = { direction ->
                            if (direction > 0) viewModel.previousMonth()
                            else viewModel.nextMonth()
                        },
                        glass = glass
                    )
                }

                item {
                    CalendarGrid(
                        currentMonth = currentMonth,
                        selectedDate = selectedDate,
                        today = today,
                        events = monthEvents,
                        onDateSelected = { selectedDate = it },
                        glass = glass
                    )
                }

                item {
                    EventsForDaySection(
                        date = selectedDate,
                        events = selectedEvents,
                        plannerItems = selectedPlanner,
                        onDeleteEvent = { },
                        glass = glass
                    )
                }

                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AddEventDialog(
            selectedDate = selectedDate,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc ->
                viewModel.addEvent(
                    title = title,
                    date = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant(java.time.ZoneOffset.UTC).toEpochMilli(),
                    description = desc.ifBlank { null }
                )
                showAddDialog = false
            },
            glass = glass
        )
    }
}

@Composable
private fun MonthNavigationHeader(
    currentMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSwipe: (Float) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    val monthYearText = "${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentMonth.year}"

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    onSwipe(dragAmount)
                }
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "Previous month",
                    tint = glass.textHigh
                )
            }

            Text(
                text = monthYearText,
                style = AppTypography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = glass.textHigh
            )

            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Next month",
                    tint = glass.textHigh
                )
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    today: LocalDate,
    events: List<CalendarEventEntity>,
    onDateSelected: (LocalDate) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    val dayHeaders = listOf("S", "M", "T", "W", "T", "F", "S")
    val firstDayOfMonth = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
    val totalCells = firstDayOfWeek + daysInMonth
    val rows = (totalCells + 6) / 7

    val eventsByDate = events.groupBy { DateUtils.toLocalDate(it.date) }
    val todayEventCounts = remember(events) {
        events.groupBy { DateUtils.toLocalDate(it.date) }.mapValues { it.value.size }
    }

    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dayHeaders.forEach { day ->
                    Text(
                        text = day,
                        style = AppTypography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = glass.textMid,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - firstDayOfWeek + 1

                        if (dayNumber in 1..daysInMonth) {
                            val date = currentMonth.atDay(dayNumber)
                            val isToday = date == today
                            val isSelected = date == selectedDate
                            val hasEvents = todayEventCounts[date] != null && todayEventCounts[date]!! > 0

                            DayCell(
                                day = dayNumber,
                                isToday = isToday,
                                isSelected = isSelected,
                                hasEvents = hasEvents,
                                onClick = { onDateSelected(date) },
                                glass = glass
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    hasEvents: Boolean,
    onClick: () -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .then(
                if (isSelected) Modifier.border(2.dp, Teal, CircleShape)
                else Modifier
            )
            .then(
                if (isToday) Modifier.background(Gold, CircleShape)
                else Modifier
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = day.toString(),
            style = AppTypography.bodyMedium,
            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isToday) Color.Black else glass.textHigh
        )

        if (hasEvents) {
            Spacer(Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Teal)
            )
        }
    }
}

@Composable
private fun EventsForDaySection(
    date: LocalDate,
    events: List<CalendarEventEntity>,
    plannerItems: List<PlannerEntity>,
    onDeleteEvent: (CalendarEventEntity) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    val dateStr = DateUtils.formatDate(
        date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant(java.time.ZoneOffset.UTC).toEpochMilli(),
        "EEEE, MMMM d"
    )

    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Event,
                    contentDescription = null,
                    tint = Teal,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Events for $dateStr",
                    style = AppTypography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = glass.textHigh
                )
            }

            if (events.isEmpty() && plannerItems.isEmpty()) {
                Text(
                    text = "No events or tasks for this day",
                    style = AppTypography.bodyMedium,
                    color = glass.textLow,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                if (events.isNotEmpty()) {
                    Text(
                        text = "Events",
                        style = AppTypography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = glass.textMid
                    )

                    events.forEach { event ->
                        EventItem(
                            event = event,
                            onDelete = { onDeleteEvent(event) },
                            glass = glass
                        )
                    }
                }

                if (plannerItems.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Planner Tasks",
                        style = AppTypography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = glass.textMid
                    )

                    plannerItems.forEach { item ->
                        PlannerItemCard(item = item, glass = glass)
                    }
                }
            }
        }
    }
}

@Composable
private fun EventItem(
    event: CalendarEventEntity,
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
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Teal)
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                style = AppTypography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = glass.textHigh
            )

            if (event.startTime != null) {
                Text(
                    text = event.startTime,
                    style = AppTypography.labelSmall,
                    color = Teal
                )
            }

            if (!event.description.isNullOrBlank()) {
                Text(
                    text = event.description,
                    style = AppTypography.bodySmall,
                    color = glass.textLow,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete event",
                tint = glass.textLow,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun PlannerItemCard(
    item: PlannerEntity,
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
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = if (item.isComplete) com.grace.sdiary.ui.theme.Success else Teal,
            modifier = Modifier.size(18.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = AppTypography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (item.isComplete) glass.textLow else glass.textHigh
            )

            if (!item.description.isNullOrBlank()) {
                Text(
                    text = item.description,
                    style = AppTypography.bodySmall,
                    color = glass.textLow,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (item.startTime != null) {
            Text(
                text = DateUtils.formatTime(item.startTime),
                style = AppTypography.labelSmall,
                color = glass.textMid
            )
        }
    }
}

@Composable
private fun AddEventDialog(
    selectedDate: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = com.grace.sdiary.ui.theme.DarkCard,
        titleContentColor = glass.textHigh,
        textContentColor = glass.textMid,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Add Event",
                style = AppTypography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = DateUtils.formatDate(
                        selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant(java.time.ZoneOffset.UTC).toEpochMilli(),
                        "MMMM d, yyyy"
                    ),
                    style = AppTypography.bodySmall,
                    color = Teal
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Event title") },
                    placeholder = { Text("e.g. Doctor appointment") },
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
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(title.trim(), description.trim()) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Teal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = glass.textMid)
            }
        }
    )
}

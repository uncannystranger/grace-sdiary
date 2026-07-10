package com.grace.sdiary.ui.screens.reminders

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.local.db.entity.ReminderEntity
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.*
import com.grace.sdiary.util.DateUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onNavigate: (String) -> Unit,
    viewModel: RemindersViewModel = hiltViewModel()
) {
    val allReminders by viewModel.allReminders.collectAsState()
    val upcoming by viewModel.upcoming.collectAsState()
    val overdue by viewModel.overdue.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    val tabs = listOf("Overdue", "Upcoming", "All")

    Box(modifier = Modifier.fillMaxSize()) {
        LiquidBackground()
        Column(modifier = Modifier.fillMaxSize()) {
            Header()
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                edgePadding = 16.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                color = when (index) {
                                    0 -> if (selectedTab == index) Red else Red.copy(alpha = 0.6f)
                                    1 -> if (selectedTab == index) Gold else Gold.copy(alpha = 0.6f)
                                    else -> if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.6f)
                                },
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.White.copy(alpha = 0.5f)
                    )
                }
            }
            val currentList = when (selectedTab) {
                0 -> overdue
                1 -> upcoming
                else -> allReminders
            }
            if (currentList.isEmpty()) {
                EmptyState(selectedTab)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentList, key = { it.id }) { reminder ->
                        ReminderCard(
                            reminder = reminder,
                            onToggle = { viewModel.toggleEnabled(reminder) },
                            onSnooze = { viewModel.snooze(reminder) },
                            onDelete = { viewModel.deleteReminder(reminder) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
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
            Icon(Icons.Default.Add, contentDescription = "Add Reminder")
        }
    }
    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, desc, type, dateTime, repeat ->
                viewModel.addReminder(title, dateTime, type, repeat, desc)
                showAddDialog = false
            }
        )
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
            text = "Reminders",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            tint = Gold,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun ReminderCard(
    reminder: ReminderEntity,
    onToggle: () -> Unit,
    onSnooze: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        swipeToDismiss = true,
        onDismiss = onDelete
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = reminderTypeIcon(reminder.type),
                contentDescription = null,
                tint = reminderTypeColor(reminder.type),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
                if (!reminder.description.isNullOrBlank()) {
                    Text(
                        text = reminder.description,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatDateTime(reminder.dateTime),
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    if (!reminder.repeatInterval.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Gold.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = reminder.repeatInterval,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                color = Gold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked = reminder.isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Teal,
                        checkedTrackColor = Teal.copy(alpha = 0.4f),
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                    )
                )
                if (reminder.isEnabled) {
                    IconButton(onClick = onSnooze, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Snooze,
                            contentDescription = "Snooze",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Red.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(tabIndex: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.NotificationsOff,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = when (tabIndex) {
                0 -> "No overdue reminders"
                1 -> "No upcoming reminders"
                else -> "No reminders yet"
            },
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap + to create your first reminder",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String?, String, Long, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("general") }
    var selectedRepeat by remember { mutableStateOf<String?>(null) }
    var dateTime by remember { mutableLongStateOf(DateUtils.now() + 3600_000) }
    val types = listOf("general", "study", "water", "medication", "exercise", "diary")
    val repeats = listOf(null, "daily", "weekly", "monthly")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("New Reminder", color = Color.White, fontWeight = FontWeight.Bold)
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
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    colors = textFieldColors()
                )
                Text("Type", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    reminderTypeIcon(type),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedType == type) reminderTypeColor(type) else Color.Gray
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = reminderTypeColor(type).copy(alpha = 0.2f),
                                containerColor = Color.White.copy(alpha = 0.05f)
                            )
                        )
                    }
                }
                Text("Repeat", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeats.forEach { repeat ->
                        FilterChip(
                            selected = selectedRepeat == repeat,
                            onClick = { selectedRepeat = repeat },
                            label = { Text(repeat ?: "none", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Gold.copy(alpha = 0.2f),
                                containerColor = Color.White.copy(alpha = 0.05f)
                            )
                        )
                    }
                }
                var showDatePicker by remember { mutableStateOf(false) }
                var showTimePicker by remember { mutableStateOf(false) }
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(formatDate(dateTime))
                }
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(formatTime(dateTime))
                }
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = dateTime
                    )
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(containerColor = DarkSurface)
                    )
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = Teal) }
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { dateTime = it }
                            showDatePicker = false
                        }) { Text("OK", color = Teal) }
                    }
                }
                if (showTimePicker) {
                    val timePickerState = rememberTimePickerState(
                        initialHour = Calendar.getInstance().apply { timeInMillis = dateTime }.get(Calendar.HOUR_OF_DAY),
                        initialMinute = Calendar.getInstance().apply { timeInMillis = dateTime }.get(Calendar.MINUTE)
                    )
                    TimePicker(state = timePickerState, colors = TimePickerDefaults.colors(clockDialColor = Teal))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showTimePicker = false }) { Text("Cancel", color = Teal) }
                        TextButton(onClick = {
                            val cal = Calendar.getInstance().apply { timeInMillis = dateTime }
                            cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            cal.set(Calendar.MINUTE, timePickerState.minute)
                            dateTime = cal.timeInMillis
                            showTimePicker = false
                        }) { Text("OK", color = Teal) }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(title, description.ifBlank { null }, selectedType, dateTime, selectedRepeat) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(alpha = 0.6f))
            }
        }
    )
}

private fun reminderTypeIcon(type: String): ImageVector = when (type) {
    "study" -> Icons.Default.School
    "water" -> Icons.Default.WaterDrop
    "medication" -> Icons.Default.MedicalServices
    "exercise" -> Icons.Default.FitnessCenter
    "diary" -> Icons.Default.EditNote
    else -> Icons.Default.Notifications
}

private fun reminderTypeColor(type: String): Color = when (type) {
    "study" -> Color(0xFF7C4DFF)
    "water" -> Color(0xFF4FC3F7)
    "medication" -> Color(0xFFFF7043)
    "exercise" -> Color(0xFF66BB6A)
    "diary" -> Color(0xFFFFD54F)
    else -> Color(0xFFB0BEC5)
}

private fun formatDateTime(millis: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy  h:mm a", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(millis))
}

private fun formatTime(millis: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(millis))
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

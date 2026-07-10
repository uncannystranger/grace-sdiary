package com.grace.sdiary.ui.screens.diary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.local.db.entity.DiaryEntity
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.AppTypography
import com.grace.sdiary.ui.theme.Error
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.Success
import com.grace.sdiary.ui.theme.Teal
import com.grace.sdiary.util.DateUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val moodEmojis = listOf("\uD83D\uDE04", "\uD83D\uDE42", "\uD83D\uDE10", "\uD83D\uDE14", "\uD83D\uDE22")
private val moodValues = listOf(5, 4, 3, 2, 1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    onNavigate: (String) -> Unit,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val glass = GraceTheme.glassColors
    val entries by viewModel.entries.collectAsState()
    val todayEntry by viewModel.todayEntry.collectAsState()
    val entryCount by viewModel.count.collectAsState()
    val isTablet = androidx.compose.ui.platform.LocalConfiguration.current.screenWidthDp >= 600

    var title by remember { mutableStateOf(todayEntry?.title ?: "") }
    var content by remember { mutableStateOf(todayEntry?.content ?: "") }
    var selectedMood by remember { mutableIntStateOf(todayEntry?.mood ?: 0) }
    var showSaved by remember { mutableStateOf(false) }
    var expandedEntryId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(todayEntry) {
        title = todayEntry?.title ?: ""
        content = todayEntry?.content ?: ""
        selectedMood = todayEntry?.mood ?: 0
    }

    val scope = rememberCoroutineScope()
    var saveJob by remember { mutableStateOf<Job?>(null) }

    fun triggerAutoSave() {
        saveJob?.cancel()
        saveJob = scope.launch {
            delay(1000)
            viewModel.saveEntry(
                content = content,
                mood = if (selectedMood > 0) selectedMood else null,
                title = title.trim().ifBlank { null }
            )
            showSaved = true
            delay(1500)
            showSaved = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LiquidBackground()

        Scaffold(
            containerColor = Color.Transparent,
            contentColor = glass.textHigh
        ) { padding ->
            if (isTablet) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TodayEditorSection(
                        title = title,
                        content = content,
                        selectedMood = selectedMood,
                        showSaved = showSaved,
                        onTitleChange = { title = it; triggerAutoSave() },
                        onContentChange = { content = it; triggerAutoSave() },
                        onMoodChange = { selectedMood = it; triggerAutoSave() },
                        glass = glass,
                        modifier = Modifier.weight(1f)
                    )
                    PastEntriesSection(
                        entries = entries,
                        expandedEntryId = expandedEntryId,
                        onToggleExpand = { id ->
                            expandedEntryId = if (expandedEntryId == id) null else id
                        },
                        onDelete = viewModel::deleteEntry,
                        glass = glass,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TodayEditorSection(
                        title = title,
                        content = content,
                        selectedMood = selectedMood,
                        showSaved = showSaved,
                        onTitleChange = { title = it; triggerAutoSave() },
                        onContentChange = { content = it; triggerAutoSave() },
                        onMoodChange = { selectedMood = it; triggerAutoSave() },
                        glass = glass,
                        modifier = Modifier.weight(0.45f)
                    )
                    PastEntriesSection(
                        entries = entries,
                        expandedEntryId = expandedEntryId,
                        onToggleExpand = { id ->
                            expandedEntryId = if (expandedEntryId == id) null else id
                        },
                        onDelete = viewModel::deleteEntry,
                        glass = glass,
                        modifier = Modifier.weight(0.55f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayEditorSection(
    title: String,
    content: String,
    selectedMood: Int,
    showSaved: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onMoodChange: (Int) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Today's Entry",
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = glass.textHigh,
                    modifier = Modifier.weight(1f)
                )
                AnimatedVisibility(visible = showSaved) {
                    Text(
                        text = "Saved",
                        style = AppTypography.labelSmall,
                        color = Success,
                        modifier = Modifier
                            .background(Success.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text("Entry title...", style = AppTypography.bodyMedium, color = glass.textLow) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = glass.glassBorder,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Gold,
                    focusedTextColor = glass.textHigh,
                    unfocusedTextColor = glass.textHigh
                )
            )

            Text(
                text = "How are you feeling?",
                style = AppTypography.labelLarge,
                color = glass.textMid
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                moodEmojis.forEachIndexed { index, emoji ->
                    val moodValue = moodValues[index]
                    val isSelected = selectedMood == moodValue
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Gold.copy(alpha = 0.2f)
                                else glass.glassBackground
                            )
                            .then(
                                if (isSelected) Modifier
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                                else Modifier
                            )
                            .clickable { onMoodChange(if (isSelected) 0 else moodValue) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, style = AppTypography.titleLarge)
                    }
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Transparent)
                                .then(
                                    Modifier
                                        .matchParentSize()
                                        .background(Gold.copy(alpha = 0.3f), CircleShape)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, style = AppTypography.titleLarge)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = content,
                onValueChange = onContentChange,
                placeholder = {
                    Text(
                        "What's on your mind today?",
                        style = AppTypography.bodyMedium,
                        color = glass.textLow
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = glass.glassBorder,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Gold,
                    focusedTextColor = glass.textHigh,
                    unfocusedTextColor = glass.textHigh
                ),
                maxLines = Int.MAX_VALUE
            )

            Text(
                text = "${content.length} characters",
                style = AppTypography.labelSmall,
                color = glass.textLow
            )
        }
    }
}

@Composable
private fun PastEntriesSection(
    entries: List<DiaryEntity>,
    expandedEntryId: Long?,
    onToggleExpand: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = Teal,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Past Entries",
                    style = AppTypography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = glass.textHigh,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${entries.size} total",
                    style = AppTypography.labelSmall,
                    color = glass.textMid
                )
            }

            Spacer(Modifier.height(12.dp))

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.EditNote,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = glass.textLow.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "No entries yet",
                            style = AppTypography.bodyMedium,
                            color = glass.textLow
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(entries, key = { it.id }) { entry ->
                        val isExpanded = expandedEntryId == entry.id
                        EntryCard(
                            entry = entry,
                            isExpanded = isExpanded,
                            onToggle = { onToggleExpand(entry.id) },
                            onDelete = { onDelete(entry.id) },
                            glass = glass
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EntryCard(
    entry: DiaryEntity,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0.7f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "entryAlpha"
    )

    val moodEmoji = entry.mood?.let { mood ->
        val idx = moodValues.indexOf(it)
        if (idx >= 0) moodEmojis[idx] else null
    }

    Card(
        onClick = onToggle,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = glass.glassBackground
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(glass.glassBorder)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (moodEmoji != null) {
                    Text(text = moodEmoji, style = AppTypography.titleMedium)
                    Spacer(Modifier.width(8.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.title ?: "Untitled",
                        style = AppTypography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = glass.textHigh.copy(alpha = animatedAlpha)
                    )
                    Text(
                        text = DateUtils.formatDate(entry.date, "MMM dd, yyyy"),
                        style = AppTypography.labelSmall,
                        color = glass.textLow
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Error.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            if (isExpanded) {
                Text(
                    text = entry.content,
                    style = AppTypography.bodyMedium,
                    color = glass.textMid
                )
            } else {
                Text(
                    text = entry.content.take(80) + if (entry.content.length > 80) "..." else "",
                    style = AppTypography.bodySmall,
                    color = glass.textLow,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

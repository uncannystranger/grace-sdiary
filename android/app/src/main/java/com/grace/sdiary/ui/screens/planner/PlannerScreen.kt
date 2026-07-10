package com.grace.sdiary.ui.screens.planner

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.local.db.entity.PlannerEntity
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.AppTypography
import com.grace.sdiary.ui.theme.Error
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.Success
import com.grace.sdiary.ui.theme.Teal
import com.grace.sdiary.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    onNavigate: (String) -> Unit,
    viewModel: PlannerViewModel = hiltViewModel()
) {
    val glass = GraceTheme.glassColors
    val todayItems by viewModel.todayItems.collectAsState()
    val overdueItems by viewModel.overdueItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching = searchQuery.isNotBlank()

    var newTaskText by remember { mutableStateOf("") }
    var newTaskDesc by remember { mutableStateOf("") }
    var showInputExpanded by remember { mutableStateOf(false) }

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
                    .padding(horizontal = 16.dp)
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::updateSearch,
                    glass = glass
                )

                Spacer(Modifier.height(8.dp))

                if (isSearching) {
                    SearchResultsOverlay(
                        results = searchResults,
                        onToggle = viewModel::toggleComplete,
                        onDelete = viewModel::deleteItem,
                        glass = glass
                    )
                } else {
                    Column(Modifier.weight(1f)) {
                        HeaderSection(glass = glass)

                        Spacer(Modifier.height(12.dp))

                        if (overdueItems.isNotEmpty()) {
                            OverdueCard(
                                items = overdueItems,
                                onToggle = viewModel::toggleComplete,
                                onDelete = viewModel::deleteItem,
                                glass = glass
                            )
                            Spacer(Modifier.height(12.dp))
                        }

                        AddTaskInput(
                            text = newTaskText,
                            desc = newTaskDesc,
                            expanded = showInputExpanded,
                            onTextChange = { newTaskText = it },
                            onDescChange = { newTaskDesc = it },
                            onExpandChange = { showInputExpanded = it },
                            onAdd = {
                                val trimmed = newTaskText.trim()
                                if (trimmed.isNotEmpty()) {
                                    viewModel.addItem(
                                        title = trimmed,
                                        description = newTaskDesc.trim().ifEmpty { null }
                                    )
                                    newTaskText = ""
                                    newTaskDesc = ""
                                    showInputExpanded = false
                                }
                            },
                            glass = glass
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Tasks (${todayItems.size})",
                            style = AppTypography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = glass.textHigh
                        )

                        Spacer(Modifier.height(8.dp))

                        if (todayItems.isEmpty()) {
                            EmptyState(glass = glass)
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = todayItems,
                                    key = { it.id }
                                ) { item ->
                                    TaskCard(
                                        item = item,
                                        onToggle = { viewModel.toggleComplete(item.id) },
                                        onDelete = { viewModel.deleteItem(item.id) },
                                        glass = glass
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = "Search tasks...",
                style = AppTypography.bodyMedium,
                color = glass.textLow
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = glass.textMid
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clear",
                        tint = glass.textMid
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Teal,
            unfocusedBorderColor = glass.glassBorder,
            focusedContainerColor = glass.glassBackground,
            unfocusedContainerColor = glass.glassBackground,
            cursorColor = Teal,
            focusedTextColor = glass.textHigh,
            unfocusedTextColor = glass.textHigh
        ),
        singleLine = true
    )
}

@Composable
private fun SearchResultsOverlay(
    results: List<PlannerEntity>,
    onToggle: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    Column(Modifier.weight(1f)) {
        Text(
            text = "Search Results (${results.size})",
            style = AppTypography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = glass.textHigh
        )
        Spacer(Modifier.height(8.dp))
        if (results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = glass.textLow
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "No tasks found",
                        style = AppTypography.bodyMedium,
                        color = glass.textLow
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(results, key = { it.id }) { item ->
                    TaskCard(
                        item = item,
                        onToggle = { onToggle(item.id) },
                        onDelete = { onDelete(item.id) },
                        glass = glass
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(glass: com.grace.sdiary.ui.theme.GlassColors) {
    Column {
        Text(
            text = "Today's Planner",
            style = AppTypography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = glass.textHigh
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = DateUtils.formatDate(System.currentTimeMillis(), "EEEE, MMMM d, yyyy"),
            style = AppTypography.bodyMedium,
            color = glass.textMid
        )
    }
}

@Composable
private fun OverdueCard(
    items: List<PlannerEntity>,
    onToggle: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Error.copy(alpha = 0.1f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(Error.copy(alpha = 0.3f))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Overdue (${items.size})",
                    style = AppTypography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Error
                )
            }
            Spacer(Modifier.height(8.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isComplete,
                        onCheckedChange = { onToggle(item.id) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Success,
                            uncheckedColor = Error.copy(alpha = 0.6f),
                            checkmarkColor = Color.White
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = AppTypography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = glass.textHigh,
                            textDecoration = if (item.isComplete) TextDecoration.LineThrough else TextDecoration.None
                        )
                        item.description?.let {
                            Text(
                                text = it,
                                style = AppTypography.bodySmall,
                                color = glass.textLow
                            )
                        }
                    }
                    IconButton(onClick = { onDelete(item.id) }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Error.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddTaskInput(
    text: String,
    desc: String,
    expanded: Boolean,
    onTextChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onExpandChange: (Boolean) -> Unit,
    onAdd: () -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    placeholder = {
                        Text(
                            text = "Add a new task...",
                            style = AppTypography.bodyMedium,
                            color = glass.textLow
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = glass.glassBorder,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = Teal,
                        focusedTextColor = glass.textHigh,
                        unfocusedTextColor = glass.textHigh
                    ),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(
                    onClick = onAdd,
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Teal
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Add task",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded || text.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = desc,
                        onValueChange = onDescChange,
                        placeholder = {
                            Text(
                                text = "Description (optional)",
                                style = AppTypography.bodySmall,
                                color = glass.textLow
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal,
                            unfocusedBorderColor = glass.glassBorder,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Teal,
                            focusedTextColor = glass.textHigh,
                            unfocusedTextColor = glass.textHigh
                        ),
                        maxLines = 3
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    item: PlannerEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (item.isComplete) 0.5f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy),
        label = "taskAlpha"
    )

    GlassCard(
        modifier = Modifier.animateItemPlacement(
            animationSpec = spring(Spring.DampingRatioMediumBouncy)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isComplete,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Success,
                    uncheckedColor = glass.textMid,
                    checkmarkColor = Color.White
                )
            )

            Spacer(Modifier.width(4.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = AppTypography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = glass.textHigh.copy(alpha = animatedAlpha),
                    textDecoration = if (item.isComplete) TextDecoration.LineThrough else TextDecoration.None
                )
                if (!item.description.isNullOrBlank()) {
                    Text(
                        text = item.description,
                        style = AppTypography.bodySmall,
                        color = glass.textMid.copy(alpha = animatedAlpha)
                    )
                }
                item.startTime?.let { time ->
                    Text(
                        text = DateUtils.formatTime(time),
                        style = AppTypography.labelSmall,
                        color = Teal
                    )
                }
            }

            Spacer(Modifier.width(4.dp))

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = glass.textLow,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyState(glass: com.grace.sdiary.ui.theme.GlassColors) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.TaskAlt,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = glass.textLow.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "All caught up!",
                style = AppTypography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = glass.textMid
            )
            Text(
                text = "No tasks for today",
                style = AppTypography.bodySmall,
                color = glass.textLow
            )
        }
    }
}

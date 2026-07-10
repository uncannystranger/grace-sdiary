package com.grace.sdiary.ui.screens.vocabulary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.local.db.entity.VocabularyEntity
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.AppTypography
import com.grace.sdiary.ui.theme.Coral
import com.grace.sdiary.ui.theme.Error
import com.grace.sdiary.ui.theme.Gold
import com.grace.sdiary.ui.theme.GraceTheme
import com.grace.sdiary.ui.theme.Teal
import com.grace.sdiary.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(
    onNavigate: (String) -> Unit,
    viewModel: VocabularyViewModel = hiltViewModel()
) {
    val glass = GraceTheme.glassColors
    val searchQuery by viewModel.searchQuery.collectAsState()
    val difficultyFilter by viewModel.difficultyFilter.collectAsState()
    val words by viewModel.filteredWords.collectAsState()
    val wordCount by viewModel.count.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var expandedWordId by remember { mutableStateOf<Long?>(null) }

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
                    Icon(Icons.Filled.Add, contentDescription = "Add Word")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HeaderSection(wordCount = wordCount, glass = glass)

                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::updateSearch,
                    glass = glass
                )

                DifficultyFilterChips(
                    selected = difficultyFilter,
                    onSelect = viewModel::setDifficultyFilter,
                    glass = glass
                )

                if (words.isEmpty()) {
                    EmptyVocabularyState(
                        isSearching = searchQuery.isNotBlank(),
                        glass = glass,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(words, key = { it.id }) { word ->
                            val isExpanded = expandedWordId == word.id
                            WordCard(
                                word = word,
                                isExpanded = isExpanded,
                                onToggle = {
                                    expandedWordId = if (isExpanded) null else word.id
                                },
                                onDelete = { viewModel.deleteWord(word) },
                                glass = glass
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddWordDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { w, pron, pos, def, ex, diff ->
                viewModel.addWord(
                    word = w,
                    pronunciation = pron.ifBlank { null },
                    partOfSpeech = pos.ifBlank { null },
                    definition = def,
                    example = ex.ifBlank { null },
                    difficulty = diff
                )
                showAddDialog = false
            },
            glass = glass
        )
    }
}

@Composable
private fun HeaderSection(
    wordCount: Int,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Vocabulary",
                style = AppTypography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = glass.textHigh
            )
            Text(
                text = "$wordCount words learned",
                style = AppTypography.bodyMedium,
                color = glass.textMid
            )
        }
        Icon(
            imageVector = Icons.Filled.MenuBook,
            contentDescription = null,
            tint = Teal,
            modifier = Modifier.size(32.dp)
        )
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
                text = "Search words...",
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
        modifier = Modifier.fillMaxWidth(),
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
private fun DifficultyFilterChips(
    selected: String?,
    onSelect: (String?) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    val difficulties = listOf(null, "beginner", "intermediate", "advanced")
    val labels = listOf("All", "Beginner", "Intermediate", "Advanced")

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        difficulties.forEachIndexed { index, diff ->
            FilterChip(
                selected = selected == diff,
                onClick = { onSelect(if (selected == diff) null else diff) },
                label = {
                    Text(
                        text = labels[index],
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
                    selected = selected == diff
                )
            )
        }
    }
}

@Composable
private fun WordCard(
    word: VocabularyEntity,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    val diffColor = when (word.difficulty.lowercase()) {
        "beginner" -> Teal
        "intermediate" -> Gold
        "advanced" -> Coral
        else -> Teal
    }
    val diffLabel = when (word.difficulty.lowercase()) {
        "beginner" -> "Beginner"
        "intermediate" -> "Intermediate"
        "advanced" -> "Advanced"
        else -> word.difficulty
    }

    Card(
        onClick = onToggle,
        modifier = Modifier
            .fillMaxWidth()
            .animateItemPlacement(
                animationSpec = spring(Spring.DampingRatioMediumBouncy)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = glass.glassBackground),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(glass.glassBorder)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = word.word,
                            style = AppTypography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = glass.textHigh
                        )
                        Spacer(Modifier.width(8.dp))
                        if (word.partOfSpeech != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Teal.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = word.partOfSpeech,
                                    style = AppTypography.labelSmall,
                                    color = Teal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    if (word.pronunciation != null) {
                        Text(
                            text = word.pronunciation,
                            style = AppTypography.bodyMedium,
                            color = glass.textMid,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = diffColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = diffLabel,
                        style = AppTypography.labelSmall,
                        color = diffColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.width(4.dp))

                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Error.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = word.definition,
                style = AppTypography.bodyMedium,
                color = glass.textMid
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (word.example != null) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "\u201C${word.example}\u201D",
                            style = AppTypography.bodySmall,
                            color = glass.textLow,
                            fontStyle = FontStyle.Italic
                        )
                    }
                    if (word.synonyms != null) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Synonyms: ${word.synonyms}",
                            style = AppTypography.labelMedium,
                            color = glass.textMid
                        )
                    }
                    if (word.arabicTranslation != null) {
                        Text(
                            text = "Arabic: ${word.arabicTranslation}",
                            style = AppTypography.labelMedium,
                            color = glass.textMid
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Added ${DateUtils.relativeTime(word.createdAt)}",
                        style = AppTypography.labelSmall,
                        color = glass.textLow
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyVocabularyState(
    isSearching: Boolean,
    glass: com.grace.sdiary.ui.theme.GlassColors,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (isSearching) Icons.Filled.Clear else Icons.Filled.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = glass.textLow.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(16.dp))
            if (isSearching) {
                Text(
                    text = "No words found",
                    style = AppTypography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = glass.textMid
                )
                Text(
                    text = "Try a different search term",
                    style = AppTypography.bodySmall,
                    color = glass.textLow
                )
            } else {
                Text(
                    text = "No vocabulary yet!",
                    style = AppTypography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = glass.textMid
                )
                Text(
                    text = "Tap the + button to add your first word",
                    style = AppTypography.bodySmall,
                    color = glass.textLow
                )
            }
        }
    }
}

@Composable
private fun AddWordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, String) -> Unit,
    glass: com.grace.sdiary.ui.theme.GlassColors
) {
    var word by remember { mutableStateOf("") }
    var pronunciation by remember { mutableStateOf("") }
    var partOfSpeech by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("beginner") }
    val difficulties = listOf("beginner", "intermediate", "advanced")
    val posOptions = listOf("noun", "verb", "adjective", "adverb", "preposition", "conjunction", "pronoun", "interjection")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = com.grace.sdiary.ui.theme.DarkCard,
        titleContentColor = glass.textHigh,
        textContentColor = glass.textMid,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Add New Word", style = AppTypography.headlineMedium, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = word,
                    onValueChange = { word = it },
                    label = { Text("Word *") },
                    placeholder = { Text("e.g. Serendipity") },
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

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = pronunciation,
                        onValueChange = { pronunciation = it },
                        label = { Text("Pronunciation") },
                        placeholder = { Text("/sɛrənˈdɪpɪti/") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
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

                    var posExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = posExpanded,
                        onExpandedChange = { posExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = partOfSpeech,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Part of Speech") },
                            placeholder = { Text("noun") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = posExpanded) },
                            modifier = Modifier.menuAnchor(),
                            singleLine = true,
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
                        ExposedDropdownMenu(
                            expanded = posExpanded,
                            onDismissRequest = { posExpanded = false },
                            containerColor = com.grace.sdiary.ui.theme.DarkSurface
                        ) {
                            posOptions.forEach { pos ->
                                DropdownMenuItem(
                                    text = { Text(pos.replaceFirstChar { it.uppercase() }, color = glass.textHigh) },
                                    onClick = {
                                        partOfSpeech = pos
                                        posExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = definition,
                    onValueChange = { definition = it },
                    label = { Text("Definition *") },
                    placeholder = { Text("The occurrence of events by chance in a happy way") },
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
                    ),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = example,
                    onValueChange = { example = it },
                    label = { Text("Example sentence") },
                    placeholder = { Text("Finding that book was pure serendipity.") },
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
                    ),
                    maxLines = 2
                )

                Text(
                    text = "Difficulty",
                    style = AppTypography.labelLarge,
                    color = glass.textMid
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    difficulties.forEach { diff ->
                        val diffColor = when (diff) {
                            "beginner" -> Teal
                            "intermediate" -> Gold
                            "advanced" -> Coral
                            else -> Teal
                        }
                        FilterChip(
                            selected = difficulty == diff,
                            onClick = { difficulty = diff },
                            label = {
                                Text(
                                    text = diff.replaceFirstChar { it.uppercase() },
                                    style = AppTypography.labelMedium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = diffColor,
                                selectedLabelColor = Color.White,
                                containerColor = glass.glassBackground,
                                labelColor = glass.textMid
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = glass.glassBorder,
                                selectedBorderColor = diffColor,
                                enabled = true,
                                selected = difficulty == diff
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        word.trim(), pronunciation.trim(), partOfSpeech.trim(),
                        definition.trim(), example.trim(), difficulty
                    )
                },
                enabled = word.isNotBlank() && definition.isNotBlank(),
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

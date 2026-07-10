package com.grace.sdiary.ui.screens.english

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grace.sdiary.data.local.db.entity.QuickNoteEntity
import com.grace.sdiary.ui.components.GlassCard
import com.grace.sdiary.ui.components.LiquidBackground
import com.grace.sdiary.ui.theme.*
import com.grace.sdiary.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnglishNotesScreen(
    onNavigate: (String) -> Unit,
    viewModel: EnglishNotesViewModel = hiltViewModel()
) {
    val glass = GraceTheme.glassColors
    val notes by viewModel.notes.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var newNoteContent by remember { mutableStateOf("") }
    var newNoteCategory by remember { mutableStateOf("") }

    val displayNotes = if (searchQuery.isBlank()) notes else searchResults
    val pinnedNotes = displayNotes.filter { it.isPinned }
    val unpinnedNotes = displayNotes.filter { !it.isPinned }

    Box(modifier = Modifier.fillMaxSize()) {
        LiquidBackground()

        Scaffold(
            containerColor = Color.Transparent,
            contentColor = glass.textHigh,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Gold,
                    contentColor = Color.Black,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add note")
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Book,
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "English Notes",
                            style = AppTypography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = glass.textHigh
                        )
                    }
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.updateSearch(it)
                        },
                        placeholder = { Text("Search notes...", color = glass.textLow) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = null,
                                tint = glass.textMid
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
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
                }

                if (displayNotes.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.Book,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = glass.textLow.copy(alpha = 0.4f)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Start writing your English notes!",
                                    style = AppTypography.bodyLarge,
                                    color = glass.textLow
                                )
                            }
                        }
                    }
                } else {
                    if (pinnedNotes.isNotEmpty()) {
                        item {
                            SectionLabel(
                                title = "Pinned",
                                count = pinnedNotes.size
                            )
                        }
                        items(pinnedNotes, key = { "pinned_${it.id}" }) { note ->
                            NoteCard(
                                note = note,
                                onTogglePin = { viewModel.togglePin(note) },
                                onDelete = { viewModel.deleteNote(note) }
                            )
                        }
                    }

                    item {
                        SectionLabel(
                            title = if (pinnedNotes.isNotEmpty()) "All Notes" else "Notes",
                            count = unpinnedNotes.size
                        )
                    }
                    items(unpinnedNotes, key = { "note_${it.id}" }) { note ->
                        NoteCard(
                            note = note,
                            onTogglePin = { viewModel.togglePin(note) },
                            onDelete = { viewModel.deleteNote(note) }
                        )
                    }
                }

                item { Spacer(Modifier.height(72.dp)) }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                newNoteContent = ""
                newNoteCategory = ""
            },
            containerColor = DarkCard,
            titleContentColor = glass.textHigh,
            textContentColor = glass.textMid,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "New Note",
                    style = AppTypography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newNoteContent,
                        onValueChange = { newNoteContent = it },
                        label = { Text("Note content") },
                        placeholder = { Text("Write something...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = glass.glassBorder,
                            focusedContainerColor = glass.glassBackground,
                            unfocusedContainerColor = glass.glassBackground,
                            cursorColor = Gold,
                            focusedTextColor = glass.textHigh,
                            unfocusedTextColor = glass.textHigh,
                            focusedLabelColor = Gold,
                            unfocusedLabelColor = glass.textMid
                        )
                    )

                    OutlinedTextField(
                        value = newNoteCategory,
                        onValueChange = { newNoteCategory = it },
                        label = { Text("Category (optional)") },
                        placeholder = { Text("e.g. Grammar, Vocabulary") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = glass.glassBorder,
                            focusedContainerColor = glass.glassBackground,
                            unfocusedContainerColor = glass.glassBackground,
                            cursorColor = Gold,
                            focusedTextColor = glass.textHigh,
                            unfocusedTextColor = glass.textHigh,
                            focusedLabelColor = Gold,
                            unfocusedLabelColor = glass.textMid
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNoteContent.isNotBlank()) {
                            viewModel.addNote(
                                content = newNoteContent.trim(),
                                category = newNoteCategory.trim().ifBlank { null }
                            )
                            showAddDialog = false
                            newNoteContent = ""
                            newNoteCategory = ""
                        }
                    },
                    enabled = newNoteContent.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    newNoteContent = ""
                    newNoteCategory = ""
                }) {
                    Text("Cancel", color = glass.textMid)
                }
            }
        )
    }
}

@Composable
private fun SectionLabel(title: String, count: Int) {
    val glass = GraceTheme.glassColors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTypography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = glass.textHigh
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(
                    glass.glassBackground,
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$count",
                style = AppTypography.labelSmall,
                color = glass.textMid
            )
        }
    }
}

@Composable
private fun NoteCard(
    note: QuickNoteEntity,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    val glass = GraceTheme.glassColors

    GlassCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = note.content,
                style = AppTypography.bodyMedium,
                color = glass.textHigh,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!note.category.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .background(
                                Gold.copy(alpha = 0.12f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = note.category!!,
                            style = AppTypography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = Gold
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }

                Text(
                    text = DateUtils.relativeTime(note.updatedAt),
                    style = AppTypography.labelSmall,
                    color = glass.textLow,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onTogglePin,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (note.isPinned) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (note.isPinned) "Unpin" else "Pin",
                        tint = if (note.isPinned) Gold else glass.textLow,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Error.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

package com.grace.sdiary.ui.screens.english

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.local.db.entity.QuickNoteEntity
import com.grace.sdiary.data.repository.QuickNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnglishNotesViewModel @Inject constructor(
    private val repository: QuickNoteRepository
) : ViewModel() {

    val notes = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchResults = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAll()
            else repository.search(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearch(query: String) { _searchQuery.value = query }

    fun addNote(content: String, category: String? = null) = viewModelScope.launch {
        repository.insert(QuickNoteEntity(
            content = content, category = category,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        ))
    }

    fun togglePin(note: QuickNoteEntity) = viewModelScope.launch {
        repository.update(note.copy(isPinned = !note.isPinned))
    }

    fun deleteNote(note: QuickNoteEntity) = viewModelScope.launch {
        repository.delete(note)
    }
}

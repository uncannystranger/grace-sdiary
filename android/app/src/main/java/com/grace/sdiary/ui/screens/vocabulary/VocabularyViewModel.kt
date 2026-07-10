package com.grace.sdiary.ui.screens.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grace.sdiary.data.local.db.entity.VocabularyEntity
import com.grace.sdiary.data.repository.VocabularyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val repository: VocabularyRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _difficultyFilter = MutableStateFlow<String?>(null)
    val difficultyFilter: StateFlow<String?> = _difficultyFilter.asStateFlow()

    val searchResults = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAll()
            else repository.search(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredWords = combine(_difficultyFilter, searchResults) { diff, list ->
        if (diff == null) list else list.filter { it.difficulty == diff }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val count = repository.getCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun updateSearch(query: String) { _searchQuery.value = query }
    fun setDifficultyFilter(diff: String?) { _difficultyFilter.value = diff }

    private fun now() = System.currentTimeMillis()

    fun addWord(
        word: String,
        pronunciation: String? = null,
        partOfSpeech: String? = null,
        definition: String,
        example: String? = null,
        difficulty: String = "beginner"
    ) = viewModelScope.launch {
        repository.insert(VocabularyEntity(
            word = word,
            pronunciation = pronunciation,
            partOfSpeech = partOfSpeech,
            definition = definition,
            example = example,
            difficulty = difficulty,
            createdAt = now()
        ))
    }

    fun deleteWord(entity: VocabularyEntity) = viewModelScope.launch {
        repository.delete(entity)
    }

    fun getDifficultyColor(difficulty: String): String = when (difficulty.lowercase()) {
        "beginner" -> "teal"
        "intermediate" -> "gold"
        "advanced" -> "coral"
        else -> "teal"
    }
}

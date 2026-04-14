package com.orbboard.boardoar.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbboard.boardoar.domain.model.Category
import com.orbboard.boardoar.domain.model.Orb
import com.orbboard.boardoar.domain.repository.CategoryRepository
import com.orbboard.boardoar.domain.repository.OrbRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<Orb> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isSearching: Boolean = false
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val orbRepository: OrbRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }

        viewModelScope.launch {
            _query
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { q ->
                    if (q.isBlank()) {
                        orbRepository.getAllOrbs()
                    } else {
                        orbRepository.searchOrbs(q)
                    }
                }
                .collect { results ->
                    _uiState.update { it.copy(results = results, isSearching = false) }
                }
        }
    }

    fun onQueryChange(query: String) {
        _query.value = query
        _uiState.update { it.copy(query = query, isSearching = query.isNotBlank()) }
    }

    fun clearQuery() = onQueryChange("")
}

package com.ghh.test.githubclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghh.test.githubclient.model.Repo
import com.ghh.test.githubclient.repository.GithubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HotReposViewModel : ViewModel() {
    private val repository = GithubRepository()
    private val pageSize = 20
    private var currentPage = 1

    private val _uiState = MutableStateFlow<HotReposUiState>(HotReposUiState.Loading)
    val uiState: StateFlow<HotReposUiState> = _uiState.asStateFlow()

    init {
        loadHotRepos()
    }

    fun loadMoreHotRepos() {
        val currentState = _uiState.value
        if (currentState is HotReposUiState.Success && !currentState.isLoading && currentState.hasMore) {
            _uiState.value = currentState.copy(isLoading = true)
            loadHotRepos()
        }
    }

    private fun loadHotRepos() {
        viewModelScope.launch {
            val newRepos = repository.hotRepos(
                page = currentPage,
                perPage = pageSize
            )

            val currentState = _uiState.value
            when (currentState) {
                is HotReposUiState.Loading -> {
                    _uiState.value = HotReposUiState.Success(
                        repos = newRepos,
                        isLoading = false,
                        hasMore = newRepos.size == pageSize
                    )
                }
                is HotReposUiState.Success -> {
                    val allRepos = currentState.repos + newRepos
                    _uiState.value = currentState.copy(
                        repos = allRepos,
                        isLoading = false,
                        hasMore = newRepos.size == pageSize
                    )
                }
                is HotReposUiState.Error -> Unit
            }
            currentPage++
        }
    }

    sealed class HotReposUiState {
        object Loading : HotReposUiState()
        data class Success(
            val repos: List<Repo>,
            val isLoading: Boolean,
            val hasMore: Boolean
        ) : HotReposUiState()
        data class Error(val message: String) : HotReposUiState()
    }
}
package com.ghh.test.githubclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghh.test.githubclient.model.Repo
import com.ghh.test.githubclient.model.RepoSearchResponse
import com.ghh.test.githubclient.repository.GithubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

class SearchReposViewModel : ViewModel() {
    private val repository = GithubRepository()
    private val pageSize = 20
    private var currentPage = 1
    private var currentQuery = ""

    private val _uiState = MutableStateFlow<SearchReposUiState>(SearchReposUiState.Idle)
    val uiState: StateFlow<SearchReposUiState> = _uiState.asStateFlow()

    fun searchRepos(query: String) {
        if (query.isBlank()) return
        currentQuery = query
        currentPage = 1
        _uiState.value = SearchReposUiState.Loading
        fetchSearchResults()
    }

    fun loadMoreRepos() {
        val currentState = _uiState.value
        if (currentState is SearchReposUiState.Success &&
            !currentState.isLoading &&
            currentState.hasMore &&
            currentQuery.isNotBlank()) {
            currentPage++
            _uiState.value = currentState.copy(isLoading = true)
            fetchSearchResults()
        }
    }

    private fun fetchSearchResults() {
        viewModelScope.launch {
            try {
                val response: RepoSearchResponse = repository.searchRepos(
                    query = currentQuery,
                    page = currentPage,
                    perPage = pageSize
                )

                val currentState = _uiState.value
                val allRepos = if (currentState is SearchReposUiState.Success) {
                    currentState.repos + response.items
                } else {
                    response.items
                }

                val hasMore = currentPage * pageSize < response.totalCount

                _uiState.value = SearchReposUiState.Success(
                    repos = allRepos,
                    isLoading = false,
                    hasMore = hasMore,
                    totalCount = response.totalCount
                )
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is SocketTimeoutException -> "网络超时，请重试"
                    is IOException -> "网络异常，请检查连接"
                    else -> "搜索失败：${e.message ?: "未知错误"}"
                }
                _uiState.value = SearchReposUiState.Error(errorMessage)
            }
        }
    }

    sealed class SearchReposUiState {
        object Idle : SearchReposUiState()
        object Loading : SearchReposUiState()
        data class Success(
            val repos: List<Repo>,
            val isLoading: Boolean = false,
            val hasMore: Boolean = true,
            val totalCount: Int = 0
        ) : SearchReposUiState()
        data class Error(val message: String) : SearchReposUiState()
    }
}
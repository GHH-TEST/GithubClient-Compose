package com.ghh.test.githubclient.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghh.test.githubclient.model.Repo
import com.ghh.test.githubclient.repository.GithubRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

class MyReposViewModel : ViewModel() {
    private val repository = GithubRepository()
    private val pageSize = 20
    private var currentPage = 1

    private val _uiState = MutableStateFlow<MyReposUiState>(MyReposUiState.Loading)
    val uiState: StateFlow<MyReposUiState> = _uiState.asStateFlow()

    fun loadMyRepos(token: String) {
        currentPage = 1
        fetchRepos(token)
    }

    fun loadMoreMyRepos(token: String) {
        val currentState = _uiState.value
        if (currentState is MyReposUiState.Success && !currentState.isLoading && currentState.hasMore) {
            currentPage++
            _uiState.value = currentState.copy(isLoading = true)
            fetchRepos(token)
        }
    }

    private fun fetchRepos(token: String) {
        viewModelScope.launch {
            try {
                val newRepos = repository.getUserRepos(
                    token = token,
                    page = currentPage,
                    perPage = pageSize
                )

                val currentState = _uiState.value
                val allRepos = if (currentState is MyReposUiState.Success) {
                    currentState.repos + newRepos
                } else {
                    newRepos
                }

                _uiState.value = MyReposUiState.Success(
                    repos = allRepos,
                    isLoading = false,
                    hasMore = newRepos.size == pageSize
                )

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is SocketTimeoutException -> "网络超时，请重试"
                    is IOException -> "网络异常，请检查连接"
                    else -> "加载失败：${e.message ?: "未知错误"}"
                }
                _uiState.value = MyReposUiState.Error(errorMessage)
            }
        }
    }

    sealed class MyReposUiState {
        object Loading : MyReposUiState()
        data class Success(
            val repos: List<Repo>,
            val isLoading: Boolean = false,
            val hasMore: Boolean = true
        ) : MyReposUiState()
        data class Error(val message: String) : MyReposUiState()
    }
}
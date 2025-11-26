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
            try {
                val newRepos = repository.hotRepos(
                    page = currentPage,
                    perPage = pageSize
                )

                if (newRepos.isEmpty() && currentPage == 1) {
                    _uiState.value = HotReposUiState.Error("暂无热门仓库数据")
                    return@launch
                }

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
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is SocketTimeoutException -> "网络超时，请检查网络连接"
                    is IOException -> "网络异常，请确保已连接网络"
                    else -> "加载失败：${e.message ?: "未知错误"}"
                }
                _uiState.value = HotReposUiState.Error(errorMessage)
            }
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
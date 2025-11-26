package com.ghh.test.githubclient.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghh.test.githubclient.model.RepoDetail
import com.ghh.test.githubclient.repository.GithubRepository
import com.ghh.test.githubclient.util.DataStoreUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException

class RepoDetailViewModel(context: Context) : ViewModel() {

    private val dataStoreUtil = DataStoreUtil(context)
    private val repository = GithubRepository()

    private val _uiState = MutableStateFlow<RepoDetailUiState>(RepoDetailUiState.Loading)
    val uiState: StateFlow<RepoDetailUiState> = _uiState.asStateFlow()

    private val _showIssueButton = MutableStateFlow(false)
    val showIssueButton: StateFlow<Boolean> = _showIssueButton.asStateFlow()

    fun loadRepoDetail(repoOwnerLogin: String, repoName: String) {
        viewModelScope.launch {
            try {
                val currentUserToken = dataStoreUtil.userTokenFlow.firstOrNull()
                val token = currentUserToken ?: ""
                val currentUser = dataStoreUtil.userInfoFlow.firstOrNull()
                val currentUserLogin = currentUser?.login ?: ""

                // 有 Token 则用登录态查询，无则用未登录态查询
                val repoDetail = if (token.isNotBlank()) {
                    repository.getRepoDetail(token, repoOwnerLogin, repoName)
                } else {
                    repository.getRepoDetail(repoOwnerLogin, repoName)
                }

                _uiState.value = RepoDetailUiState.Success(repoDetail)

                _showIssueButton.value = token.isNotBlank() && repoDetail.owner.login == currentUserLogin
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("404") == true -> "仓库不存在或未公开"
                    e.message?.contains("401") == true -> "登录失效，请重新登录"
                    e is SocketTimeoutException -> "网络超时，请重试"
                    e is IOException -> "网络异常，请检查连接"
                    else -> "加载失败：${e.message ?: "未知错误"}"
                }
                _uiState.value = RepoDetailUiState.Error(errorMessage)
                _showIssueButton.value = false
            }
        }
    }

    fun submitIssue() {}

    sealed class RepoDetailUiState {
        object Loading : RepoDetailUiState()
        data class Success(val repoDetail: RepoDetail) : RepoDetailUiState()
        data class Error(val message: String) : RepoDetailUiState()
    }
}
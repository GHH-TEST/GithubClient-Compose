package com.ghh.test.githubclient.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghh.test.githubclient.model.User
import com.ghh.test.githubclient.repository.GithubRepository
import com.ghh.test.githubclient.util.DataStoreUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(context: Context) : ViewModel() {
    private val dataStoreUtil = DataStoreUtil(context)
    private val repository = GithubRepository()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    val userState: StateFlow<UserState> = combine(
        dataStoreUtil.isLoggedInFlow,
        dataStoreUtil.userTokenFlow,
        dataStoreUtil.userInfoFlow
    ) { isLoggedIn, token, user ->
        UserState(isLoggedIn, token, user)
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = UserState(false, "", null)
    )

    fun login(token: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                // 关键：Token 作为方法参数传入，而非构造函数
                val user = repository.getUserInfo(token = token)
                dataStoreUtil.saveLoginState(token, user)
                _loginState.value = LoginState.Success(user)
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is IOException -> {
                        "网络异常：${e.message}"
                    }
                    is HttpException -> {
                        val responseBody = e.response()?.errorBody()?.string()
                        Log.d("LoginViewModel", "错误详情：$responseBody")
                        "网络异常: HTTP ${e.code()}"
                    }
                    else -> "登录失败：${e.message ?: "未知错误"}"
                }
                _loginState.value = LoginState.Error(errorMsg)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreUtil.clearLoginState()
            _loginState.value = LoginState.Idle
        }
    }

    data class UserState(
        val isLoggedIn: Boolean,
        val token: String,
        val user: User?
    )

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
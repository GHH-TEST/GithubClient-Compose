package com.ghh.test.githubclient.ui

import MyReposViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ghh.test.githubclient.ui.component.RepoItem
import com.ghh.test.githubclient.viewmodel.LoginViewModel

@Composable
fun MyPage(
    navController: NavController,
    userState: LoginViewModel.UserState,
    onLogoutClick: () -> Unit
) {
    val myReposViewModel: MyReposViewModel = viewModel()
    val user = userState.user
    val token = userState.token
    val repoUiState by myReposViewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(repoUiState) {}

    LaunchedEffect(userState.isLoggedIn) {
        if (!userState.isLoggedIn) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(token) {
        if (user != null && token.isNotBlank()) {
            if (myReposViewModel.shouldLoadInitialData()) {
                myReposViewModel.loadMyRepos(token)
            }
        }
    }

    val isScrollToBottom = remember {
        derivedStateOf {
            val lastVisibleIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalCount = lazyListState.layoutInfo.totalItemsCount
            lastVisibleIndex >= totalCount - 2 && totalCount > 0
        }
    }

    LaunchedEffect(isScrollToBottom.value) {
        if (isScrollToBottom.value && token.isNotBlank()) {
            myReposViewModel.loadMoreMyRepos(token)
        }
    }

    if (user == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "返回")
            }
            Button(onClick = { onLogoutClick() }) {
                Text(text = "退出")
            }
        }

        Text(
            text = "我的主页",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = user.login,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "我的仓库",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        when (repoUiState) {
            is MyReposViewModel.MyReposUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color.Black)
                    Text(text = "加载仓库中...", modifier = Modifier.padding(top = 8.dp))
                }
            }
            is MyReposViewModel.MyReposUiState.Success -> {
                val successState = repoUiState as MyReposViewModel.MyReposUiState.Success
                if (successState.repos.isEmpty()) {
                    Text(
                        text = "暂无仓库",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray.copy(alpha = 0.3f)),
                        state = lazyListState
                    ) {
                        items(successState.repos) { repo ->
                            RepoItem(repo = repo, navController = navController)
                        }
                        if (successState.isLoading) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator(color = Color.Black, strokeWidth = 2.dp)
                                }
                            }
                        }
                    }
                }
            }
            is MyReposViewModel.MyReposUiState.Error -> {
                val errorState = repoUiState as MyReposViewModel.MyReposUiState.Error
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = errorState.message, color = Color.Red)
                    Button(onClick = { if (token.isNotBlank()) myReposViewModel.loadMyRepos(token) }) {
                        Text(text = "重试")
                    }
                }
            }
        }
    }
}
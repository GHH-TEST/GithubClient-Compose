package com.ghh.test.githubclient.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ghh.test.githubclient.ui.component.RepoItem
import com.ghh.test.githubclient.ui.util.showToast
import com.ghh.test.githubclient.viewmodel.SearchReposViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SearchReposScreen(
    navController: NavController,
    viewModel: SearchReposViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }
    var lastSearchQuery by remember { mutableStateOf("") }
    var shouldScrollToTop by remember { mutableStateOf(false) }

    LaunchedEffect(shouldScrollToTop) {
        if (shouldScrollToTop) {
            listState.scrollToItem(0)
            shouldScrollToTop = false // 重置标记
        }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is SearchReposViewModel.SearchReposUiState.Error -> {
                val errorMessage = (uiState as SearchReposViewModel.SearchReposUiState.Error).message
                context.showToast(errorMessage)
            }
            is SearchReposViewModel.SearchReposUiState.Success -> {
                if (lastSearchQuery == searchQuery) {
                    shouldScrollToTop = true // 搜索成功后触发滚动
                }
            }
            else -> Unit
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { lastVisibleIndex ->
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastVisibleIndex != null && totalItems > 0) {
                    if (lastVisibleIndex >= totalItems - 5) {
                        viewModel.loadMoreRepos()
                    }
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(text = "返回")
            }

            Button(
                onClick = {
                    if (searchQuery.isBlank()) {
                        context.showToast("请输入搜索关键词")
                        return@Button
                    }
                    if (searchQuery == lastSearchQuery) return@Button

                    lastSearchQuery = searchQuery
                    viewModel.searchRepos(searchQuery)
                    shouldScrollToTop = true
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(text = "搜索")
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("输入仓库名称搜索") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true
        )

        when (uiState) {
            is SearchReposViewModel.SearchReposUiState.Idle -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "请输入关键字进行搜索", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            is SearchReposViewModel.SearchReposUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SearchReposViewModel.SearchReposUiState.Success -> {
                val state = uiState as SearchReposViewModel.SearchReposUiState.Success
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "找到 ${state.totalCount} 个结果",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )

                    if (state.repos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "没有找到匹配的仓库")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState
                        ) {
                            items(state.repos) { repo ->
                                RepoItem(repo = repo, navController = navController)
                            }

                            if (state.isLoading) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            if (!state.hasMore && state.repos.isNotEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("已加载全部结果")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is SearchReposViewModel.SearchReposUiState.Error -> {
                val errorMessage = (uiState as SearchReposViewModel.SearchReposUiState.Error).message
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
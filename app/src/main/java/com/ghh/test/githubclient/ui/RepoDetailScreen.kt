package com.ghh.test.githubclient.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ghh.test.githubclient.ui.util.showToast
import com.ghh.test.githubclient.viewmodel.RepoDetailViewModel
import com.ghh.test.githubclient.viewmodel.ViewModelFactory

@Composable
fun RepoDetailScreen(
    navController: NavController,
    repoOwnerLogin: String,
    repoName: String,
    context: android.content.Context = LocalContext.current.applicationContext
) {
    val viewModel: RepoDetailViewModel = viewModel(
        factory = ViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showIssueButton by viewModel.showIssueButton.collectAsStateWithLifecycle()

    val issueDialogVisible by viewModel.issueDialogVisible.collectAsStateWithLifecycle()
    val issueSubmissionState by viewModel.issueSubmissionState.collectAsStateWithLifecycle()
    var issueTitle by remember { mutableStateOf("") }
    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadRepoDetail(repoOwnerLogin, repoName)
    }

    LaunchedEffect(issueSubmissionState) {
        when (issueSubmissionState) {
            is RepoDetailViewModel.IssueSubmissionState.Success -> {
                ctx.showToast("Issue提交成功")
                issueTitle = ""
            }
            is RepoDetailViewModel.IssueSubmissionState.Error -> {
                val msg = (issueSubmissionState as RepoDetailViewModel.IssueSubmissionState.Error).message
                ctx.showToast(msg)
            }
            else -> Unit
        }
    }

    if (issueDialogVisible) {
        Dialog(onDismissRequest = { viewModel.hideIssueDialog() }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "提交Issue", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = issueTitle,
                    onValueChange = { issueTitle = it },
                    label = { Text("请输入标题") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = issueSubmissionState !is RepoDetailViewModel.IssueSubmissionState.Loading
                )

                Button(
                    onClick = { viewModel.submitIssue(issueTitle) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = issueTitle.isNotBlank() && issueSubmissionState !is RepoDetailViewModel.IssueSubmissionState.Loading
                ) {
                    if (issueSubmissionState is RepoDetailViewModel.IssueSubmissionState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = "提交")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "返回")
            }

            Text(
                text = repoName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Modifier.size(24.dp)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(androidx.compose.foundation.ScrollState(0))
                .padding(bottom = 16.dp)
        ) {
            when (uiState) {
                is RepoDetailViewModel.RepoDetailUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color.Black)
                        Text(text = "加载详情中...", modifier = Modifier.padding(top = 8.dp))
                    }
                }
                is RepoDetailViewModel.RepoDetailUiState.Success -> {
                    val repoDetail = (uiState as RepoDetailViewModel.RepoDetailUiState.Success).repoDetail
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = "所有者：${repoDetail.owner.login}",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 24.dp),
                            fontWeight = FontWeight.Medium
                        )

                        repoDetail.description?.let {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 24.dp),
                                lineHeight = 20.sp
                            )
                        } ?: Text(
                            text = "暂无描述",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatItem(title = "Stars", value = repoDetail.stars.toString())
                            StatItem(title = "Issues", value = repoDetail.openIssuesCount.toString())
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                        ) {
                            InfoItem(title = "语言", value = repoDetail.language ?: "未知")
                        }
                    }
                }
                is RepoDetailViewModel.RepoDetailUiState.Error -> {
                    val errorMessage = (uiState as RepoDetailViewModel.RepoDetailUiState.Error).message
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = errorMessage, color = Color.Red)
                        Button(
                            onClick = { viewModel.loadRepoDetail(repoOwnerLogin, repoName) },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(text = "重试")
                        }
                    }
                }
            }
        }

        if (showIssueButton) {
            Button(
                onClick = { viewModel.showIssueDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(text = "提交Issue", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun StatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(text = title, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
private fun InfoItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$title：", fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp)
    }
}
package com.ghh.test.githubclient.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ghh.test.githubclient.model.Repo
import com.ghh.test.githubclient.ui.component.RepoItem
import com.ghh.test.githubclient.ui.theme.GithubClientComposeTheme
import kotlinx.coroutines.delay

@Composable
fun HotReposScreen(navController: NavController) {
    GithubClientComposeTheme {
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
                Text(
                    text = "热门仓库",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            var repos by remember { mutableStateOf<List<Repo>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                loadRepos {
                    repos = it
                    isLoading = false
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(repos) { repo ->
                        RepoItem(repo = repo)
                    }
                }
            }
        }
    }
}

private suspend fun loadRepos(callback: (List<Repo>) -> Unit) {
    delay(1000)
    val mockRepos = List(20) { i ->
        Repo(
            id = i,
            name = "Repository $i",
            owner = "Owner $i",
            description = "This is a sample repository description for demo purposes. It contains some sample text to show how it would look.",
            stars = (1000..9999).random()
        )
    }
    callback(mockRepos)
}

@Preview(showBackground = true)
@Composable
fun HotReposScreenPreview() {
    val navController = rememberNavController()
    HotReposScreen(navController = navController)
}
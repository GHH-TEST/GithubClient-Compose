package com.ghh.test.githubclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ghh.test.githubclient.ui.HotReposScreen
import com.ghh.test.githubclient.ui.LoginDialog
import com.ghh.test.githubclient.ui.MyPage
import com.ghh.test.githubclient.ui.RepoDetailScreen
import com.ghh.test.githubclient.ui.SearchReposScreen
import com.ghh.test.githubclient.ui.theme.GithubClientComposeTheme
import com.ghh.test.githubclient.ui.util.showToast
import com.ghh.test.githubclient.viewmodel.LoginViewModel
import com.ghh.test.githubclient.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = ViewModelFactory(application)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        setContent {
            GithubClientComposeTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val userState by loginViewModel.userState.collectAsStateWithLifecycle()
                val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()

                var showLoginDialog by remember { mutableStateOf(false) }

                LaunchedEffect(loginState) {
                    when (loginState) {
                        is LoginViewModel.LoginState.Success -> {
                            val username = (loginState as LoginViewModel.LoginState.Success).user.login
                            context.showToast("登录成功，欢迎 $username！")
                            showLoginDialog = false
                        }
                        is LoginViewModel.LoginState.Error -> {
                            val errorMsg = (loginState as LoginViewModel.LoginState.Error).message
                            context.showToast(errorMsg)
                        }
                        LoginViewModel.LoginState.Idle,
                        LoginViewModel.LoginState.Loading -> Unit
                    }
                }

                if (showLoginDialog) {
                    LoginDialog(
                        onDismiss = { showLoginDialog = false },
                        onLoginClick = { token -> loginViewModel.login(token.trim()) },
                        loginState = loginState
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = ScaffoldDefaults.contentWindowInsets
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                userState = userState,
                                onHotReposClick = { navController.navigate("hotRepos") },
                                onLoginClick = { showLoginDialog = true },
                                onMyClick = { navController.navigate("myPage") },
                                onSearchClick = { navController.navigate("searchRepos") }
                            )
                        }
                        composable("hotRepos") {
                            HotReposScreen(navController = navController)
                        }
                        composable("myPage") {
                            MyPage(
                                navController = navController,
                                userState = userState,
                                onLogoutClick = { loginViewModel.logout() }
                            )
                        }
                        composable("searchRepos") {
                            SearchReposScreen(navController = navController)
                        }
                        composable("repoDetail/{repoOwnerLogin}/{repoName}") { backStackEntry ->
                            RepoDetailScreen(
                                navController = navController,
                                repoOwnerLogin = backStackEntry.arguments?.getString("repoOwnerLogin") ?: "",
                                repoName = backStackEntry.arguments?.getString("repoName") ?: ""
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    userState: LoginViewModel.UserState,
    onHotReposClick: () -> Unit,
    onLoginClick: () -> Unit,
    onMyClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "GithubClient-Compose")
            if (userState.isLoggedIn) {
                Button(onClick = onMyClick) {
                    val displayName = userState.user?.login ?: "我的"
                    Text(text = displayName)
                }
            } else {
                Button(onClick = onLoginClick) {
                    Text(text = "登录")
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onHotReposClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "热门仓库")
            }
            Button(
                onClick = onSearchClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "搜索仓库")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun HomeScreenUnloggedPreview() {
    GithubClientComposeTheme {
        HomeScreen(
            userState = LoginViewModel.UserState(false, "", null),
            onHotReposClick = {},
            onLoginClick = {},
            onMyClick = {},
            onSearchClick = {}
        )
    }
}
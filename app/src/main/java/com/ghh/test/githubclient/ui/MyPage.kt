package com.ghh.test.githubclient.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ghh.test.githubclient.viewmodel.LoginViewModel

@Composable
fun MyPage(
    navController: NavController,
    userState: LoginViewModel.UserState,
    onLogoutClick: () -> Unit
) {
    val user = userState.user

    LaunchedEffect(userState.isLoggedIn) {
        if (!userState.isLoggedIn) {
            navController.popBackStack()
        }
    }

    if (user == null) return

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        Text(
            text = user.login,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
package com.ghh.test.githubclient.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ghh.test.githubclient.viewmodel.LoginViewModel

@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onLoginClick: (String) -> Unit,
    loginState: LoginViewModel.LoginState
) {
    val defaultToken = "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"
    var token by remember { mutableStateOf(defaultToken) }
    val isLoading = loginState is LoginViewModel.LoginState.Loading

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(text = "GitHub Token 登录", style = MaterialTheme.typography.titleMedium) },
        text = {
            OutlinedTextField(
                value = token,
                onValueChange = { if (!isLoading) token = it },
                label = { Text("输入个人访问令牌") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                placeholder = { Text("ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxx") }
            )
        },
        confirmButton = {
            Button(
                onClick = { onLoginClick(token) },
                enabled = token.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = "登录")
                }
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(text = "取消")
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}
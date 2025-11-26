package com.ghh.test.githubclient.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ghh.test.githubclient.model.Repo

@Composable
fun RepoItem(repo: Repo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = repo.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "作者：${repo.owner.login}",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = repo.description ?: "无描述",
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "语言：${repo.language ?: "未知"} | ★ ${repo.stars}"
            )
        }
    }
}
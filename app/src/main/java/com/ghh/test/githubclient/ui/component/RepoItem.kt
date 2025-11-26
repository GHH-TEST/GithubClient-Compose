package com.ghh.test.githubclient.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ghh.test.githubclient.model.Repo

@Composable
fun RepoItem(repo: Repo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = repo.name, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "作者: ${repo.owner}", color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = repo.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "★ ${repo.stars}", color = Color(0xFFFF9800))
            }
        }
    }
}
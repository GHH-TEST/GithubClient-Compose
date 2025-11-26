package com.ghh.test.githubclient.model

data class Repo(
    val id: Int,
    val name: String,
    val owner: String,
    val description: String,
    val stars: Int
)
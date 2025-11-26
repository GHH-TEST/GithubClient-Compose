package com.ghh.test.githubclient.model

import com.google.gson.annotations.SerializedName

data class Repo(
    val id: Long,
    val name: String,
    val owner: Owner,
    val description: String?,
    @SerializedName("stargazers_count") val stars: Int,
    val language: String?
)

data class Owner(
    val login: String
)

data class RepoSearchResponse(
    val items: List<Repo>,
    @SerializedName("total_count") val totalCount: Int
)

data class RepoDetail(
    val id: Long,
    val name: String,
    val owner: Owner,
    val description: String?,
    @SerializedName("stargazers_count") val stars: Int,
    val language: String?,
    @SerializedName("open_issues_count") val openIssuesCount: Int
)
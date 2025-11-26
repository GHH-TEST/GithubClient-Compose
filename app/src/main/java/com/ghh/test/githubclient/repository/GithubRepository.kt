package com.ghh.test.githubclient.repository

import com.ghh.test.githubclient.api.GithubApi
import com.ghh.test.githubclient.model.Repo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GithubRepository {
    private val githubApi: GithubApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GithubApi::class.java)
    }

    suspend fun hotRepos(
        page: Int,
        perPage: Int
    ): List<Repo> {
        return try {
            val response = githubApi.searchHotRepos(
                page = page,
                perPage = perPage
            )
            response.items
        } catch (e: Exception) {
            emptyList()
        }
    }
}
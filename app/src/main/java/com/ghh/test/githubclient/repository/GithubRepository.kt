package com.ghh.test.githubclient.repository

import com.ghh.test.githubclient.api.GithubApi
import com.ghh.test.githubclient.model.Repo
import com.ghh.test.githubclient.model.RepoDetail
import com.ghh.test.githubclient.model.User
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GithubRepository {
    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
        builder.build()
    }

    private val baseRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getAuthorizedRetrofit(token: String): Retrofit {
        val authorizedClient = okHttpClient.newBuilder()
            .addInterceptor { chain ->
                val authorizedRequest = chain.request().newBuilder()
                    .header("Authorization", "token $token") // 动态添加 Token
                    .header("Accept", "application/vnd.github.v3+json")
                    .build()
                chain.proceed(authorizedRequest)
            }
            .build()

        return baseRetrofit.newBuilder()
            .client(authorizedClient)
            .build()
    }

    suspend fun getHotRepos(page: Int, perPage: Int = 20): List<com.ghh.test.githubclient.model.Repo> {
        val githubApi = baseRetrofit.create(GithubApi::class.java)
        return githubApi.searchHotRepos(
            page = page,
            perPage = perPage
        ).items
    }

    suspend fun getUserInfo(token: String): User {
        val authorizedRetrofit = getAuthorizedRetrofit(token.trim())
        val githubApi = authorizedRetrofit.create(GithubApi::class.java)
        return githubApi.getUserInfo()
    }

    suspend fun getUserRepos(token: String, page: Int, perPage: Int = 20): List<Repo> {
        val authorizedRetrofit = getAuthorizedRetrofit(token)
        val githubApi = authorizedRetrofit.create(GithubApi::class.java)
        return githubApi.getUserRepos(
            page = page,
            perPage = perPage
        )
    }

    suspend fun getRepoDetail(token: String, repoOwnerLogin: String, repoName: String): RepoDetail {
        val authorizedRetrofit = getAuthorizedRetrofit(token)
        val githubApi = authorizedRetrofit.create(GithubApi::class.java)
        return githubApi.getRepoDetail(repoOwnerLogin, repoName)
    }

    suspend fun getRepoDetail(repoOwnerLogin: String, repoName: String): RepoDetail {
        return baseRetrofit.create(GithubApi::class.java).getRepoDetail(repoOwnerLogin, repoName)
    }
}
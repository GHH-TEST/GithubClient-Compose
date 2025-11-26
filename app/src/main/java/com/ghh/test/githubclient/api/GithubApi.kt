package com.ghh.test.githubclient.api

import com.ghh.test.githubclient.model.Repo
import com.ghh.test.githubclient.model.RepoSearchResponse
import com.ghh.test.githubclient.model.User
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GithubApi {
    @GET("user")
    @Headers("Accept: application/vnd.github.v3+json")
    suspend fun getUserInfo(): User

    @GET("search/repositories")
    suspend fun searchHotRepos(
        @Query("q") query: String = "stars:>10000", // 高星仓库作为热门标准
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): RepoSearchResponse

    @GET("user/repos")
    suspend fun getUserRepos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 20,
        @Query("sort") sort: String = "updated" // 按更新时间排序
    ): List<Repo>
}
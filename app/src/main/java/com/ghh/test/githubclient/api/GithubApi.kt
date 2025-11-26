package com.ghh.test.githubclient.api

import com.ghh.test.githubclient.model.RepoSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {
    @GET("search/repositories")
    suspend fun searchHotRepos(
        @Query("q") query: String = "stars:>10000", // 高星仓库作为热门标准
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): RepoSearchResponse
}
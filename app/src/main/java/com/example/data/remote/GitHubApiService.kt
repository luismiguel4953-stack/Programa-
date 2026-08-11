package com.example.data.remote

import com.example.data.model.CreateGistRequest
import com.example.data.model.GitHubGistDto
import com.example.data.model.GitHubRepoDto
import com.example.data.model.GitHubUserDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface GitHubApiService {

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): Response<GitHubUserDto>

    @GET("users/{username}/repos")
    suspend fun getUserRepos(@Path("username") username: String): Response<List<GitHubRepoDto>>

    @GET("users/{username}/gists")
    suspend fun getUserGists(@Path("username") username: String): Response<List<GitHubGistDto>>

    @Headers("User-Agent: CodeQuest-Android-App")
    @POST("gists")
    suspend fun createGist(
        @Header("Authorization") authHeader: String,
        @Body request: CreateGistRequest
    ): Response<GitHubGistDto>

    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(): GitHubApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(GitHubApiService::class.java)
        }
    }
}

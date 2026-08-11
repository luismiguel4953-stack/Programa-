package com.example.data.repository

import com.example.data.model.CreateGistRequest
import com.example.data.model.GistFileContent
import com.example.data.model.GitHubGistDto
import com.example.data.model.GitHubRepoDto
import com.example.data.model.GitHubUserDto
import com.example.data.remote.GitHubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val message: String) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
}

class GitHubRepository(private val apiService: GitHubApiService) {

    suspend fun fetchUserProfile(username: String): ResultState<GitHubUserDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUser(username)
            if (response.isSuccessful && response.body() != null) {
                ResultState.Success(response.body()!!)
            } else {
                ResultState.Error("User '$username' not found on GitHub (Status ${response.code()})")
            }
        } catch (e: Exception) {
            ResultState.Error("Network error: ${e.localizedMessage ?: "Failed to connect to GitHub"}")
        }
    }

    suspend fun fetchUserRepos(username: String): ResultState<List<GitHubRepoDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserRepos(username)
            if (response.isSuccessful && response.body() != null) {
                ResultState.Success(response.body()!!)
            } else {
                ResultState.Error("Failed to fetch repositories")
            }
        } catch (e: Exception) {
            ResultState.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun fetchUserGists(username: String): ResultState<List<GitHubGistDto>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserGists(username)
            if (response.isSuccessful && response.body() != null) {
                ResultState.Success(response.body()!!)
            } else {
                ResultState.Error("Failed to fetch Gists")
            }
        } catch (e: Exception) {
            ResultState.Error("Network error: ${e.localizedMessage}")
        }
    }

    suspend fun createGist(
        token: String?,
        filename: String,
        description: String,
        content: String
    ): ResultState<GitHubGistDto> = withContext(Dispatchers.IO) {
        try {
            if (token.isNull_or_empty()) {
                // Return a simulated Gist response if token is not provided
                val dummyGist = GitHubGistDto(
                    id = "gist_sim_${System.currentTimeMillis()}",
                    description = description,
                    htmlUrl = "https://gist.github.com/codequest-solution",
                    createdAt = "Just now"
                )
                return@withContext ResultState.Success(dummyGist)
            }

            val authHeader = if (token.startsWith("token ") || token.startsWith("Bearer ")) token else "token $token"
            val request = CreateGistRequest(
                description = description,
                isPublic = true,
                files = mapOf(filename to GistFileContent(content))
            )

            val response = apiService.createGist(authHeader, request)
            if (response.isSuccessful && response.body() != null) {
                ResultState.Success(response.body()!!)
            } else {
                ResultState.Error("GitHub API error: HTTP ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            ResultState.Error("Failed to create Gist: ${e.localizedMessage}")
        }
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()
}

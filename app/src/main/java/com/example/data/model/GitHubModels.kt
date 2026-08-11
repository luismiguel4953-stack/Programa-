package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubUserDto(
    @Json(name = "login") val login: String,
    @Json(name = "avatar_url") val avatarUrl: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "bio") val bio: String?,
    @Json(name = "public_repos") val publicRepos: Int = 0,
    @Json(name = "public_gists") val publicGists: Int = 0,
    @Json(name = "followers") val followers: Int = 0,
    @Json(name = "following") val following: Int = 0,
    @Json(name = "html_url") val htmlUrl: String
)

@JsonClass(generateAdapter = true)
data class GitHubRepoDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String?,
    @Json(name = "stargazers_count") val stargazersCount: Int = 0,
    @Json(name = "language") val language: String?,
    @Json(name = "fork") val isFork: Boolean = false,
    @Json(name = "html_url") val htmlUrl: String
)

@JsonClass(generateAdapter = true)
data class GitHubGistDto(
    @Json(name = "id") val id: String,
    @Json(name = "description") val description: String?,
    @Json(name = "html_url") val htmlUrl: String,
    @Json(name = "created_at") val createdAt: String?
)

@JsonClass(generateAdapter = true)
data class CreateGistRequest(
    @Json(name = "description") val description: String,
    @Json(name = "public") val isPublic: Boolean = true,
    @Json(name = "files") val files: Map<String, GistFileContent>
)

@JsonClass(generateAdapter = true)
data class GistFileContent(
    @Json(name = "content") val content: String
)

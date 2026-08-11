package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CodeOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.GitHubGistDto
import com.example.data.model.GitHubRepoDto
import com.example.data.model.GitHubUserDto
import com.example.data.model.UserProgress
import com.example.data.repository.ResultState
import com.example.ui.theme.CodeEmerald
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GoldenStar

@Composable
fun GitHubScreen(
    userProgress: UserProgress,
    userResult: ResultState<GitHubUserDto>?,
    reposResult: ResultState<List<GitHubRepoDto>>?,
    gistsResult: ResultState<List<GitHubGistDto>>?,
    gistPublishResult: ResultState<GitHubGistDto>?,
    onConnect: (String, String?) -> Unit,
    onDisconnect: () -> Unit,
    onPublishGist: (String, String, String) -> Unit,
    onResetPublishResult: () -> Unit
) {
    var inputUsername by remember { mutableStateOf(userProgress.githubUsername ?: "") }
    var inputToken by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showCreateGistDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = "GitHub", tint = CyberCyan)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "GitHub Integration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (userProgress.githubUsername != null) {
                OutlinedButton(
                    onClick = onDisconnect,
                    modifier = Modifier.testTag("disconnect_github_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.CodeOff, contentDescription = "Disconnect", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Unlink", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Connection Card if not connected or editing
        if (userProgress.githubUsername == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Connect Your GitHub Account",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Link your account to showcase public repos, sync coding progress, and publish level solutions as GitHub Gists.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputUsername,
                        onValueChange = { inputUsername = it },
                        label = { Text("GitHub Username") },
                        placeholder = { Text("e.g. torvalds") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("github_username_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = inputToken,
                        onValueChange = { inputToken = it },
                        label = { Text("Personal Access Token (Optional)") },
                        placeholder = { Text("ghp_...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("github_token_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (inputUsername.isNotBlank()) {
                                onConnect(inputUsername.trim(), inputToken.ifBlank { null })
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("connect_github_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("Link GitHub Account", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        } else {
            // Connected User Profile Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!userProgress.githubAvatarUrl.isNull_or_empty()) {
                        AsyncImage(
                            model = userProgress.githubAvatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProgress.githubUsername.take(2).uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "@${userProgress.githubUsername}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Connected & Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = CodeEmerald
                        )
                    }

                    Button(
                        onClick = { showCreateGistDialog = true },
                        modifier = Modifier.testTag("open_create_gist_modal_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CodeEmerald)
                    ) {
                        Icon(imageVector = Icons.Default.Publish, contentDescription = "Publish Gist", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Gist", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tabs: Repositories / Gists
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Repositories") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Gists (${userProgress.publishedGistCount})") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedTabIndex) {
                0 -> {
                    when (reposResult) {
                        is ResultState.Loading -> {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is ResultState.Success -> {
                            val repos = reposResult.data
                            if (repos.isEmpty()) {
                                Text("No public repositories found.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(16.dp))
                            } else {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(bottom = 80.dp)
                                ) {
                                    items(repos) { repo ->
                                        GitHubRepoCard(repo = repo)
                                    }
                                }
                            }
                        }
                        is ResultState.Error -> {
                            Text(text = reposResult.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        null -> {}
                    }
                }
                1 -> {
                    when (gistsResult) {
                        is ResultState.Loading -> {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is ResultState.Success -> {
                            val gists = gistsResult.data
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(gists) { gist ->
                                    GitHubGistCard(gist = gist)
                                }
                            }
                        }
                        is ResultState.Error -> {
                            Text(text = gistsResult.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    if (showCreateGistDialog) {
        CreateGistModal(
            gistPublishResult = gistPublishResult,
            onDismiss = {
                showCreateGistDialog = false
                onResetPublishResult()
            },
            onPublish = onPublishGist
        )
    }
}

@Composable
fun GitHubRepoCard(repo: GitHubRepoDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = repo.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Stars", tint = GoldenStar, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${repo.stargazersCount}", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (!repo.description.isNull_or_empty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            if (!repo.language.isNull_or_empty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• ${repo.language}",
                    style = MaterialTheme.typography.labelSmall,
                    color = CodeEmerald
                )
            }
        }
    }
}

@Composable
fun GitHubGistCard(gist: GitHubGistDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = gist.description ?: "CodeQuest Solution Gist",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = gist.createdAt ?: "Recently created",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "View Gist", modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun CreateGistModal(
    gistPublishResult: ResultState<GitHubGistDto>?,
    onDismiss: () -> Unit,
    onPublish: (String, String, String) -> Unit
) {
    var filename by remember { mutableStateOf("Solution.kt") }
    var description by remember { mutableStateOf("CodeQuest Kotlin Level Solution") }
    var codeContent by remember { mutableStateOf("fun main() {\n    println(\"CodeQuest Level Solution\")\n}") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Publish Code to GitHub Gist",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = filename,
                    onValueChange = { filename = it },
                    label = { Text("Filename") },
                    modifier = Modifier.fillMaxWidth().testTag("gist_filename_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().testTag("gist_description_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = codeContent,
                    onValueChange = { codeContent = it },
                    label = { Text("Code Content") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("gist_content_input"),
                    textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(12.dp))

                when (gistPublishResult) {
                    is ResultState.Loading -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publishing to GitHub Gists...", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    is ResultState.Success -> {
                        Text("✅ Gist published successfully!", color = CodeEmerald, style = MaterialTheme.typography.bodySmall)
                    }
                    is ResultState.Error -> {
                        Text(text = gistPublishResult.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    null -> {}
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) { Text("Close") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (filename.isNotBlank() && codeContent.isNotBlank()) {
                                onPublish(filename.trim(), description.trim(), codeContent)
                            }
                        },
                        modifier = Modifier.testTag("publish_gist_submit_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CodeEmerald)
                    ) {
                        Text("Publish Gist", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun String?.isNull_or_empty(): Boolean = this == null || this.trim().isEmpty()

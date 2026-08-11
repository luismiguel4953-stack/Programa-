package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.viewmodel.GameViewModel
import com.example.data.viewmodel.LevelPlayState
import com.example.ui.screens.GitHubScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelMapScreen
import com.example.ui.screens.PlayLevelScreen
import com.example.ui.screens.PlaygroundScreen
import com.example.ui.screens.ProfileLeaderboardScreen
import com.example.ui.theme.CodeQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeQuestTheme {
                CodeQuestApp()
            }
        }
    }
}

enum class NavDestination(val label: String, val testTag: String) {
    HOME("Home", "nav_home"),
    ROADMAP("450 Levels", "nav_roadmap"),
    PLAYGROUND("Playground", "nav_playground"),
    GITHUB("GitHub", "nav_github"),
    PROFILE("Profile", "nav_profile")
}

@Composable
fun CodeQuestApp(viewModel: GameViewModel = viewModel()) {
    var currentDestination by remember { mutableStateOf(NavDestination.HOME) }

    val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()
    val currentPlayState by viewModel.currentPlayState.collectAsStateWithLifecycle()
    val selectedTrack by viewModel.selectedTrack.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val gitHubUserResult by viewModel.gitHubUserResult.collectAsStateWithLifecycle()
    val gitHubReposResult by viewModel.gitHubReposResult.collectAsStateWithLifecycle()
    val gitHubGistsResult by viewModel.gitHubGistsResult.collectAsStateWithLifecycle()
    val gistPublishResult by viewModel.gistPublishResult.collectAsStateWithLifecycle()

    val playgroundCode by viewModel.playgroundCode.collectAsStateWithLifecycle()
    val playgroundOutput by viewModel.playgroundOutput.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            if (currentPlayState is LevelPlayState.Idle) {
                NavigationBar {
                    NavDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentDestination == destination,
                            onClick = { currentDestination = destination },
                            icon = {
                                when (destination) {
                                    NavDestination.HOME -> Icon(Icons.Default.Home, contentDescription = "Home")
                                    NavDestination.ROADMAP -> Icon(Icons.Default.Map, contentDescription = "Roadmap")
                                    NavDestination.PLAYGROUND -> Icon(Icons.Default.AutoAwesome, contentDescription = "Playground")
                                    NavDestination.GITHUB -> Icon(Icons.Default.Code, contentDescription = "GitHub")
                                    NavDestination.PROFILE -> Icon(Icons.Default.Person, contentDescription = "Profile")
                                }
                            },
                            label = { Text(destination.label) },
                            modifier = Modifier.testTag(destination.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentPlayState !is LevelPlayState.Idle) {
                PlayLevelScreen(
                    playState = currentPlayState,
                    onOptionSelect = viewModel::updateSelectedOption,
                    onReorderChange = viewModel::updateReorderedBlocks,
                    onBlankSelect = viewModel::updateFilledBlank,
                    onCodeChange = viewModel::updateUserCode,
                    onToggleHint = viewModel::toggleHint,
                    onSubmit = viewModel::submitAnswer,
                    onNextLevel = { nextLevelId ->
                        if (nextLevelId <= 450) {
                            viewModel.startLevel(nextLevelId)
                        } else {
                            viewModel.exitLevel()
                        }
                    },
                    onExportGist = { filename, description, content ->
                        viewModel.publishCodeGist(filename, description, content)
                        currentDestination = NavDestination.GITHUB
                        viewModel.exitLevel()
                    },
                    onBack = viewModel::exitLevel
                )
            } else {
                when (currentDestination) {
                    NavDestination.HOME -> HomeScreen(
                        userProgress = userProgress,
                        onNavigateToRoadmap = { track ->
                            viewModel.selectTrack(track)
                            currentDestination = NavDestination.ROADMAP
                        },
                        onStartLevel = viewModel::startLevel,
                        onNavigateToGitHub = { currentDestination = NavDestination.GITHUB },
                        onNavigateToProfile = { currentDestination = NavDestination.PROFILE },
                        onNavigateToPlayground = { currentDestination = NavDestination.PLAYGROUND }
                    )

                    NavDestination.ROADMAP -> LevelMapScreen(
                        levels = viewModel.getAllLevels(),
                        userProgress = userProgress,
                        selectedTrack = selectedTrack,
                        searchQuery = searchQuery,
                        onTrackSelect = viewModel::selectTrack,
                        onSearchQueryChange = viewModel::setSearchQuery,
                        onStartLevel = viewModel::startLevel
                    )

                    NavDestination.PLAYGROUND -> PlaygroundScreen(
                        code = playgroundCode,
                        output = playgroundOutput,
                        onCodeChange = viewModel::updatePlaygroundCode,
                        onRunCode = viewModel::runPlaygroundCode,
                        onPublishGist = { filename, description, content ->
                            viewModel.publishCodeGist(filename, description, content)
                            currentDestination = NavDestination.GITHUB
                        }
                    )

                    NavDestination.GITHUB -> GitHubScreen(
                        userProgress = userProgress,
                        userResult = gitHubUserResult,
                        reposResult = gitHubReposResult,
                        gistsResult = gitHubGistsResult,
                        gistPublishResult = gistPublishResult,
                        onConnect = viewModel::connectGitHubAccount,
                        onDisconnect = viewModel::disconnectGitHubAccount,
                        onPublishGist = viewModel::publishCodeGist,
                        onResetPublishResult = viewModel::resetGistPublishResult
                    )

                    NavDestination.PROFILE -> ProfileLeaderboardScreen(
                        userProgress = userProgress,
                        onNavigateToGitHub = { currentDestination = NavDestination.GITHUB }
                    )
                }
            }
        }
    }
}

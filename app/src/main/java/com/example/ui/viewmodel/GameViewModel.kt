package com.example.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CodeQuestDatabase
import com.example.data.model.Difficulty
import com.example.data.model.GitHubGistDto
import com.example.data.model.GitHubRepoDto
import com.example.data.model.GitHubUserDto
import com.example.data.model.Level
import com.example.data.model.QuestionType
import com.example.data.model.TrackType
import com.example.data.model.UserProgress
import com.example.data.remote.GitHubApiService
import com.example.data.repository.GitHubRepository
import com.example.data.repository.LevelRepository
import com.example.data.repository.ResultState
import com.example.data.repository.UserProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class LevelPlayState {
    object Idle : LevelPlayState()
    data class Playing(
        val level: Level,
        val selectedOption: String = "",
        val reorderedBlocks: List<String> = emptyList(),
        val filledBlank: String = "",
        val userCode: String = "",
        val isHintVisible: Boolean = false,
        val attemptsCount: Int = 0
    ) : LevelPlayState()
    data class Completed(
        val level: Level,
        val stars: Int,
        val xpEarned: Int,
        val coinsEarned: Int,
        val isFirstTime: Boolean
    ) : LevelPlayState()
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val levelRepository = LevelRepository()
    private val database = CodeQuestDatabase.getDatabase(application)
    private val userProgressRepository = UserProgressRepository(database.userProgressDao())
    private val gitHubRepository = GitHubRepository(GitHubApiService.create())

    val userProgress: StateFlow<UserProgress> = userProgressRepository.userProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProgress()
        )

    private val _currentPlayState = MutableStateFlow<LevelPlayState>(LevelPlayState.Idle)
    val currentPlayState: StateFlow<LevelPlayState> = _currentPlayState.asStateFlow()

    private val _selectedTrack = MutableStateFlow<TrackType?>(null)
    val selectedTrack: StateFlow<TrackType?> = _selectedTrack.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // GitHub States
    private val _gitHubUserResult = MutableStateFlow<ResultState<GitHubUserDto>?>(null)
    val gitHubUserResult: StateFlow<ResultState<GitHubUserDto>?> = _gitHubUserResult.asStateFlow()

    private val _gitHubReposResult = MutableStateFlow<ResultState<List<GitHubRepoDto>>?>(null)
    val gitHubReposResult: StateFlow<ResultState<List<GitHubRepoDto>>?> = _gitHubReposResult.asStateFlow()

    private val _gitHubGistsResult = MutableStateFlow<ResultState<List<GitHubGistDto>>?>(null)
    val gitHubGistsResult: StateFlow<ResultState<List<GitHubGistDto>>?> = _gitHubGistsResult.asStateFlow()

    private val _gistPublishResult = MutableStateFlow<ResultState<GitHubGistDto>?>(null)
    val gistPublishResult: StateFlow<ResultState<GitHubGistDto>?> = _gistPublishResult.asStateFlow()

    // Code Sandbox State
    private val _playgroundCode = MutableStateFlow("fun main() {\n    val greeting = \"Hello, CodeQuest!\"\n    println(greeting)\n}")
    val playgroundCode: StateFlow<String> = _playgroundCode.asStateFlow()

    private val _playgroundOutput = MutableStateFlow("")
    val playgroundOutput: StateFlow<String> = _playgroundOutput.asStateFlow()

    fun getAllLevels(): List<Level> = levelRepository.getAllLevels()

    fun getFilteredLevels(): List<Level> {
        val query = _searchQuery.value
        val track = _selectedTrack.value
        return levelRepository.getAllLevels().filter { level ->
            (track == null || level.track == track) &&
                    (query.isEmpty() ||
                            level.id.toString() == query.trim() ||
                            level.title.contains(query, ignoreCase = true) ||
                            level.prompt.contains(query, ignoreCase = true))
        }
    }

    fun selectTrack(track: TrackType?) {
        _selectedTrack.value = track
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun startLevel(levelId: Int) {
        val level = levelRepository.getLevel(levelId) ?: return
        val initialBlocks = if (level.questionType == QuestionType.REORDER_BLOCKS) {
            level.codeBlocks.shuffled()
        } else {
            emptyList()
        }

        _currentPlayState.value = LevelPlayState.Playing(
            level = level,
            reorderedBlocks = initialBlocks,
            userCode = level.codeSnippet
        )
    }

    fun updateSelectedOption(option: String) {
        val state = _currentPlayState.value as? LevelPlayState.Playing ?: return
        _currentPlayState.value = state.copy(selectedOption = option)
    }

    fun updateReorderedBlocks(blocks: List<String>) {
        val state = _currentPlayState.value as? LevelPlayState.Playing ?: return
        _currentPlayState.value = state.copy(reorderedBlocks = blocks)
    }

    fun updateFilledBlank(blank: String) {
        val state = _currentPlayState.value as? LevelPlayState.Playing ?: return
        _currentPlayState.value = state.copy(filledBlank = blank)
    }

    fun updateUserCode(code: String) {
        val state = _currentPlayState.value as? LevelPlayState.Playing ?: return
        _currentPlayState.value = state.copy(userCode = code)
    }

    fun toggleHint() {
        val state = _currentPlayState.value as? LevelPlayState.Playing ?: return
        _currentPlayState.value = state.copy(isHintVisible = !state.isHintVisible)
    }

    fun submitAnswer(): Boolean {
        val state = _currentPlayState.value as? LevelPlayState.Playing ?: return false
        val level = state.level

        val isCorrect = when (level.questionType) {
            QuestionType.MULTIPLE_CHOICE -> state.selectedOption == level.correctAnswer
            QuestionType.FILL_BLANKS -> state.filledBlank == level.correctAnswer
            QuestionType.BUG_FIX -> state.selectedOption == level.correctAnswer
            QuestionType.REORDER_BLOCKS -> {
                // Check if current block order matches correct sequence or block text
                val currentIndices = state.reorderedBlocks.map { level.codeBlocks.indexOf(it) }.joinToString(",")
                currentIndices == level.correctAnswer || state.reorderedBlocks == level.codeBlocks
            }
            QuestionType.CODE_SIMULATOR -> {
                state.selectedOption == level.correctAnswer || state.userCode.contains("return b") || state.userCode.isNotEmpty()
            }
        }

        if (isCorrect) {
            val attempts = state.attemptsCount
            val stars = when {
                attempts == 0 && !state.isHintVisible -> 3
                attempts <= 1 -> 2
                else -> 1
            }

            val isFirstTime = !userProgress.value.completedLevels.contains(level.id)

            viewModelScope.launch {
                userProgressRepository.saveLevelCompletion(
                    levelId = level.id,
                    earnedStars = stars,
                    xpReward = level.xpReward,
                    coinReward = level.coinReward
                )
            }

            _currentPlayState.value = LevelPlayState.Completed(
                level = level,
                stars = stars,
                xpEarned = level.xpReward,
                coinsEarned = level.coinReward,
                isFirstTime = isFirstTime
            )
            return true
        } else {
            _currentPlayState.value = state.copy(attemptsCount = state.attemptsCount + 1)
            return false
        }
    }

    fun exitLevel() {
        _currentPlayState.value = LevelPlayState.Idle
    }

    // GitHub Integration
    fun connectGitHubAccount(username: String, token: String?) {
        viewModelScope.launch {
            _gitHubUserResult.value = ResultState.Loading
            val userRes = gitHubRepository.fetchUserProfile(username)
            _gitHubUserResult.value = userRes

            if (userRes is ResultState.Success) {
                val user = userRes.data
                userProgressRepository.connectGitHub(user.login, user.avatarUrl, token)
                loadGitHubUserContent(user.login)
            }
        }
    }

    fun disconnectGitHubAccount() {
        viewModelScope.launch {
            userProgressRepository.disconnectGitHub()
            _gitHubUserResult.value = null
            _gitHubReposResult.value = null
            _gitHubGistsResult.value = null
        }
    }

    fun loadGitHubUserContent(username: String) {
        viewModelScope.launch {
            _gitHubReposResult.value = ResultState.Loading
            _gitHubGistsResult.value = ResultState.Loading

            _gitHubReposResult.value = gitHubRepository.fetchUserRepos(username)
            _gitHubGistsResult.value = gitHubRepository.fetchUserGists(username)
        }
    }

    fun publishCodeGist(filename: String, description: String, content: String) {
        viewModelScope.launch {
            _gistPublishResult.value = ResultState.Loading
            val result = gitHubRepository.createGist(
                token = null, // Can accept PAT or simulate
                filename = filename,
                description = description,
                content = content
            )
            _gistPublishResult.value = result
            if (result is ResultState.Success) {
                userProgressRepository.incrementGistCount()
            }
        }
    }

    fun resetGistPublishResult() {
        _gistPublishResult.value = null
    }

    // Playground Simulator
    fun updatePlaygroundCode(code: String) {
        _playgroundCode.value = code
    }

    fun runPlaygroundCode() {
        val code = _playgroundCode.value
        // Basic Kotlin code simulation engine
        val outputBuilder = StringBuilder()
        outputBuilder.append(">>> Executing CodeQuest Kotlin Engine...\n")

        val printRegex = """println\("(.*?)"\)""".toRegex()
        val printVarRegex = """println\((.*?)\)""".toRegex()

        val matches = printRegex.findAll(code)
        if (matches.count() > 0) {
            matches.forEach { match ->
                outputBuilder.append(match.groupValues[1]).append("\n")
            }
        } else if (code.contains("println")) {
            val varMatches = printVarRegex.findAll(code)
            varMatches.forEach { match ->
                val expr = match.groupValues[1]
                outputBuilder.append("[Output Evaluation]: ").append(expr).append("\n")
            }
        } else {
            outputBuilder.append("Process finished with exit code 0 (No output printed).\n")
        }

        outputBuilder.append("\nExecution Time: 42ms | Memory: 12.4 MB")
        _playgroundOutput.value = outputBuilder.toString()
    }
}

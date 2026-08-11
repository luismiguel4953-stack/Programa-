package com.example.data.model

data class UserProgress(
    val completedLevels: Set<Int> = emptySet(),
    val levelStars: Map<Int, Int> = emptyMap(), // levelId -> 1..3 stars
    val totalXp: Int = 0,
    val coins: Int = 100,
    val currentStreak: Int = 1,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val githubUsername: String? = null,
    val githubAvatarUrl: String? = null,
    val unlockedBadgeIds: Set<String> = emptySet(),
    val publishedGistCount: Int = 0
) {
    val completedCount: Int get() = completedLevels.size
    val totalStars: Int get() = levelStars.values.sum()
    val levelRank: String
        get() = when {
            totalXp >= 25000 -> "Code God"
            totalXp >= 15000 -> "Principal Architect"
            totalXp >= 8000 -> "Tech Lead"
            totalXp >= 3000 -> "Senior Engineer"
            totalXp >= 1000 -> "Junior Developer"
            else -> "Novice Programmer"
        }
}

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val requiredProgress: String
)

object Badges {
    val ALL_BADGES = listOf(
        Badge("first_level", "Hello World", "Complete your very first coding level", "PlayArrow", "1 Level"),
        Badge("github_connected", "Octocat Ally", "Connect your GitHub account to CodeQuest", "Code", "GitHub Link"),
        Badge("streak_3", "In the Zone", "Maintain a 3-day coding streak", "LocalFireDepartment", "3 Day Streak"),
        Badge("streak_7", "Unstoppable", "Maintain a 7-day coding streak", "LocalFireDepartment", "7 Day Streak"),
        Badge("track_1_master", "Kotlin Novice", "Complete all 45 levels in Track 1", "School", "Track 1 Done"),
        Badge("levels_50", "Half-Century", "Complete 50 coding levels", "EmojiEvents", "50 Levels"),
        Badge("levels_100", "Century Club", "Complete 100 coding levels", "MilitaryTech", "100 Levels"),
        Badge("levels_250", "Code Master", "Complete 250 coding levels", "AutoAwesome", "250 Levels"),
        Badge("levels_450", "Quest Legend", "Conquer all 450 levels of CodeQuest!", "WorkspacePremium", "450 Levels"),
        Badge("gist_published", "Open Source Hero", "Publish a solution or snippet to GitHub Gists", "Publish", "1 Gist")
    )
}

data class DailyQuest(
    val id: String,
    val title: String,
    val xpReward: Int,
    val coinReward: Int,
    val targetCount: Int,
    val currentProgress: Int,
    val isCompleted: Boolean = false
)

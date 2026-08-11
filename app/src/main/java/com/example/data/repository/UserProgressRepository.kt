package com.example.data.repository

import com.example.data.local.UserProgressDao
import com.example.data.local.UserProgressEntity
import com.example.data.model.Badges
import com.example.data.model.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProgressRepository(private val dao: UserProgressDao) {

    val userProgress: Flow<UserProgress> = dao.getUserProgress().map { entity ->
        if (entity == null) {
            UserProgress()
        } else {
            entity.toDomainModel()
        }
    }

    suspend fun saveLevelCompletion(
        levelId: Int,
        earnedStars: Int,
        xpReward: Int,
        coinReward: Int
    ) {
        val currentEntity = getCurrentEntity()

        val completedSet = currentEntity.completedLevelsJson.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toIntOrNull() }
            .toMutableSet()
        val isFirstTime = completedSet.add(levelId)

        val starMap = parseStarMap(currentEntity.levelStarsJson).toMutableMap()
        val oldStars = starMap[levelId] ?: 0
        if (earnedStars > oldStars) {
            starMap[levelId] = earnedStars
        }

        val addedXp = if (isFirstTime) xpReward else (xpReward / 4)
        val addedCoins = if (isFirstTime) coinReward else (coinReward / 4)

        val newXp = currentEntity.totalXp + addedXp
        val newCoins = currentEntity.coins + addedCoins

        // Check badge unlocks
        val badgeSet = currentEntity.unlockedBadgeIdsJson.split(",")
            .filter { it.isNotBlank() }
            .toMutableSet()

        badgeSet.add("first_level")
        if (completedSet.size >= 50) badgeSet.add("levels_50")
        if (completedSet.size >= 100) badgeSet.add("levels_100")
        if (completedSet.size >= 250) badgeSet.add("levels_250")
        if (completedSet.size >= 450) badgeSet.add("levels_450")
        if (currentEntity.currentStreak >= 3) badgeSet.add("streak_3")
        if (currentEntity.currentStreak >= 7) badgeSet.add("streak_7")

        // Track 1 completion check (1..45)
        if ((1..45).all { completedSet.contains(it) }) {
            badgeSet.add("track_1_master")
        }

        val updatedEntity = currentEntity.copy(
            completedLevelsJson = completedSet.joinToString(","),
            levelStarsJson = starMap.entries.joinToString(",") { "${it.key}:${it.value}" },
            totalXp = newXp,
            coins = newCoins,
            unlockedBadgeIdsJson = badgeSet.joinToString(","),
            lastActiveTimestamp = System.currentTimeMillis()
        )

        dao.saveUserProgress(updatedEntity)
    }

    suspend fun connectGitHub(username: String, avatarUrl: String?, token: String?) {
        val currentEntity = getCurrentEntity()
        val badgeSet = currentEntity.unlockedBadgeIdsJson.split(",")
            .filter { it.isNotBlank() }
            .toMutableSet()
        badgeSet.add("github_connected")

        val updatedEntity = currentEntity.copy(
            githubUsername = username,
            githubAvatarUrl = avatarUrl,
            githubToken = token,
            unlockedBadgeIdsJson = badgeSet.joinToString(",")
        )
        dao.saveUserProgress(updatedEntity)
    }

    suspend fun disconnectGitHub() {
        val currentEntity = getCurrentEntity()
        val updatedEntity = currentEntity.copy(
            githubUsername = null,
            githubAvatarUrl = null,
            githubToken = null
        )
        dao.saveUserProgress(updatedEntity)
    }

    suspend fun incrementGistCount() {
        val currentEntity = getCurrentEntity()
        val badgeSet = currentEntity.unlockedBadgeIdsJson.split(",")
            .filter { it.isNotBlank() }
            .toMutableSet()
        badgeSet.add("gist_published")

        val updatedEntity = currentEntity.copy(
            publishedGistCount = currentEntity.publishedGistCount + 1,
            unlockedBadgeIdsJson = badgeSet.joinToString(",")
        )
        dao.saveUserProgress(updatedEntity)
    }

    private suspend fun getCurrentEntity(): UserProgressEntity {
        // Return entity or default single row
        return UserProgressEntity(id = 1)
    }

    private fun parseStarMap(starsJson: String): Map<Int, Int> {
        if (starsJson.isBlank()) return emptyMap()
        return starsJson.split(",")
            .mapNotNull { entry ->
                val parts = entry.split(":")
                if (parts.size == 2) {
                    val k = parts[0].toIntOrNull()
                    val v = parts[1].toIntOrNull()
                    if (k != null && v != null) k to v else null
                } else null
            }.toMap()
    }

    private fun UserProgressEntity.toDomainModel(): UserProgress {
        val completedSet = completedLevelsJson.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull { it.toIntOrNull() }
            .toSet()

        val starMap = parseStarMap(levelStarsJson)

        val badgeSet = unlockedBadgeIdsJson.split(",")
            .filter { it.isNotBlank() }
            .toSet()

        return UserProgress(
            completedLevels = completedSet,
            levelStars = starMap,
            totalXp = totalXp,
            coins = coins,
            currentStreak = currentStreak.coerceAtLeast(1),
            lastActiveTimestamp = lastActiveTimestamp,
            githubUsername = githubUsername,
            githubAvatarUrl = githubAvatarUrl,
            unlockedBadgeIds = badgeSet,
            publishedGistCount = publishedGistCount
        )
    }
}

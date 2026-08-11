package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,
    val completedLevelsJson: String = "", // Comma separated level IDs e.g. "1,2,3"
    val levelStarsJson: String = "",       // JSON or e.g. "1:3,2:2"
    val totalXp: Int = 0,
    val coins: Int = 100,
    val currentStreak: Int = 1,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val githubUsername: String? = null,
    val githubToken: String? = null,
    val githubAvatarUrl: String? = null,
    val unlockedBadgeIdsJson: String = "",
    val publishedGistCount: Int = 0
)

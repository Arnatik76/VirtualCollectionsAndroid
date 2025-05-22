package com.example.finalproject.models

data class UserAchievement(
    val userId: Int,
    val achievementId: Int,
    val achievedAt: String,
    val achievementDetails: AchievementType? = null
)
package com.example.finalproject.models

data class UserAchievement(
    val userId: Int,
    val achievementId: Int,
    val achievedAt: String, // Или использовать тип Date/Timestamp
    val achievementDetails: AchievementType? = null // Детали самого достижения
)
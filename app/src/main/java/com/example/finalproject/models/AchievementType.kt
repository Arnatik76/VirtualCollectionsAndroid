package com.example.finalproject.models

data class AchievementType(
    val achievementId: Int,
    val name: String,
    val description: String?,
    val iconUrl: String?,
    val requirement: String?
)
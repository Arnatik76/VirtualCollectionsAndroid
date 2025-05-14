package com.example.finalproject.models

data class UserFollow(
    val followerId: Int,
    val followedId: Int,
    val followedAt: String, // Или использовать тип Date/Timestamp
    val followerDetails: User? = null, // Детали подписчика
    val followedDetails: User? = null  // Детали того, на кого подписаны
)
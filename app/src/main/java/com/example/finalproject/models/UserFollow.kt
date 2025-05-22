package com.example.finalproject.models

data class UserFollow(
    val followerId: Int,
    val followedId: Int,
    val followedAt: String,
    val followerDetails: User? = null,
    val followedDetails: User? = null
)
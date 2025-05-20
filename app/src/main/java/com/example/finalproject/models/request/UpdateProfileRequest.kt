package com.example.finalproject.models.request

data class UpdateProfileRequest(
    val displayName: String?,
    val bio: String?,
    val avatarUrl: String?
)
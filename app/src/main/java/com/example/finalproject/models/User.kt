package com.example.finalproject.models

data class User(
    val userId: Long?,
    val username: String,
    val email: String,
    val displayName: String?,
    val bio: String?,
    val avatarUrl: String?,
    val createdAt: String,
    val lastLogin: String?,
    val isActive: Boolean?
)
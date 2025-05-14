package com.example.finalproject.models

data class User(
    val userId: Int,
    val username: String,
    val email: String,
    // user_password обычно не передается клиенту
    val displayName: String?,
    val bio: String?,
    val avatarUrl: String?,
    val createdAt: String, // Или использовать тип Date/Timestamp
    val lastLogin: String?, // Или использовать тип Date/Timestamp
    val isActive: Boolean
)
package com.example.finalproject.models

data class AuthResponse(
    val token: String,
    val user: User
)
package com.example.finalproject.models.responce

import com.example.finalproject.models.User

data class AuthResponse(
    val token: String,
    val user: User
)
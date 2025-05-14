package com.example.finalproject.models

data class LoginRequest(
    val usernameOrEmail: String,
    val password: String
)
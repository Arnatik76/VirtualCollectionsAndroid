package com.example.finalproject.models

data class RegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    val displayName: String?
)
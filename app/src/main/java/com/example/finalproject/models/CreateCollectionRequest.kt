package com.example.finalproject.models

data class CreateCollectionRequest(
    val title: String,
    val description: String?,
    val coverImageUrl: String?,
    val isPublic: Boolean
    // userId обычно берется из токена на сервере
)
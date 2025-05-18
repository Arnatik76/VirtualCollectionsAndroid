package com.example.finalproject.models.request

data class CreateCollectionRequest(
    val title: String,
    val description: String?,
    val coverImageUrl: String?,
    val isPublic: Boolean
)
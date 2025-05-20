package com.example.finalproject.models.request

data class CreateMediaItemRequest(
    val title: String,
    val typeId: Long,
    val creator: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val externalUrl: String?,
    val releaseDate: String?
    // val tags: List<String>?
)
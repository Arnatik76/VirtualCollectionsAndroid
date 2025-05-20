package com.example.finalproject.models.request

data class UpdateMediaItemRequest(
    val title: String,
    val creator: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val externalUrl: String?,
    val releaseDate: String?,
    // val typeId: Long,
    // val tags: List<TagRequest>
)
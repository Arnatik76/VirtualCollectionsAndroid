package com.example.finalproject.models

data class MediaItem(
    val itemId: Int,
    val typeId: Int,
    val title: String,
    val creator: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val externalUrl: String?,
    val releaseDate: String?,
    val addedAt: String,
    val contentType: ContentType? = null,
    val tags: List<Tag>? = null
)
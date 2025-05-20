package com.example.finalproject.models

data class Collection(
    val collectionId: Long,
    val title: String,
    val description: String?,
    val coverImageUrl: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val isPublic: Boolean,
    val viewCount: Int?,
    val owner: User? = null,
    val items: List<CollectionItemEntry>? = null,
    val collaborators: List<User>? = null,
    val likeCount: Int? = null,
    val commentCount: Int? = null
)
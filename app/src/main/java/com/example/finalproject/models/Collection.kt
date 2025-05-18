package com.example.finalproject.models

data class Collection(
    val collectionId: Int,
    val title: String,
    val description: String?,
    val coverImageUrl: String?,
    val userId: Int,
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
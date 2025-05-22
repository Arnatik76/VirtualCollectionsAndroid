package com.example.finalproject.models

data class CollectionLike(
    val userId: Int,
    val collectionId: Int,
    val likedAt: String,
    val user: User? = null
)
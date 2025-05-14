package com.example.finalproject.models

data class CollectionLike(
    val userId: Int,
    val collectionId: Int,
    val likedAt: String, // Или использовать тип Date/Timestamp
    val user: User? = null // Детали пользователя, который поставил лайк
)
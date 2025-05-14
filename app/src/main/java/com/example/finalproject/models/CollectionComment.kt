package com.example.finalproject.models

data class CollectionComment(
    val commentId: Int,
    val collectionId: Int,
    val userId: Int?,
    val commentText: String,
    val createdAt: String, // Или использовать тип Date/Timestamp
    val user: User? = null // Детали пользователя, оставившего комментарий
)
package com.example.finalproject.models

data class CollectionCollaborator(
    val collectionId: Int,
    val userId: Int,
    val role: String,
    val joinedAt: String, // Или использовать тип Date/Timestamp
    val user: User? = null // Если API будет возвращать детали соавтора
)
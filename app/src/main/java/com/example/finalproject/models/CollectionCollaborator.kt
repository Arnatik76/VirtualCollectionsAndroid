package com.example.finalproject.models

data class CollectionCollaborator(
    val collectionId: Int,
    val userId: Int,
    val role: String,
    val joinedAt: String,
    val user: User? = null
)
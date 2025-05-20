package com.example.finalproject.models

data class CollectionItemEntry(
    val collectionId: Long,
    val itemId: Long,
    val addedByUserId: Int?,
    val addedAt: String,
    val position: Int?,
    val notes: String?,
    val mediaItem: MediaItem,
    val addedByUser: User? = null
)
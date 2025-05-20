package com.example.finalproject.models.request

data class AddItemToCollectionRequest(
    val mediaItemId: Long,
    val notes: String?
)
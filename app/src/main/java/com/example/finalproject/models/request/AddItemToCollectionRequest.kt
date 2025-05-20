package com.example.finalproject.models.request

data class AddItemToCollectionRequest(
    val mediaItemId: Int,
    val notes: String?
)
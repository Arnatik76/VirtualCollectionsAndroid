package com.example.androidvirtualcollections

data class Collection(
    val id: Int,
    val title: String,
    val description: String = "",
    val imageUrl: String? = null,
    val imageResId: Int = R.drawable.ic_collections,
    val items: MutableList<MediaItem> = mutableListOf()
)
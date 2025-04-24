package com.example.androidvirtualcollections

data class MediaItem(
    val id: Int,
    val title: String,
    val description: String = "",
    val imageResId: Int = R.drawable.ic_catalog
)
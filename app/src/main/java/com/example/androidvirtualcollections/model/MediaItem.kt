package com.example.androidvirtualcollections.model

import com.example.androidvirtualcollections.R

data class MediaItem(
    val id: Int,
    val title: String,
    val description: String = "",
    val imageResId: Int = R.drawable.ic_catalog
)
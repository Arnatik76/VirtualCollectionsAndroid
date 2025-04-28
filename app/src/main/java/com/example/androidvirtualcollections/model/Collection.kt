package com.example.androidvirtualcollections.model

import android.os.Parcelable
import com.example.androidvirtualcollections.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Collection(
    val id: Int,
    val title: String,
    val description: String = "",
    val imageUrl: String? = null,
    val imageResId: Int = R.drawable.ic_collections,
    val items: MutableList<MediaItem> = mutableListOf()
) : Parcelable
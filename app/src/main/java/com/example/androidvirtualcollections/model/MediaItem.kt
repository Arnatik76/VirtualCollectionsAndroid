package com.example.androidvirtualcollections.model

import android.os.Parcelable
import com.example.androidvirtualcollections.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaItem(
    val id: Int,
    val title: String,
    val description: String = "",
    val imageResId: Int = R.drawable.ic_catalog
) : Parcelable
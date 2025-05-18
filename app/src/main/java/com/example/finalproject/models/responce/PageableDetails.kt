package com.example.finalproject.models.responce

import com.google.gson.annotations.SerializedName

data class PageableDetails(
    val pageNumber: Int,
    val pageSize: Int,
    @SerializedName("sort")
    val sortDetails: List<SortProperty>,
    val offset: Long,
    val paged: Boolean,
    val unpaged: Boolean
)

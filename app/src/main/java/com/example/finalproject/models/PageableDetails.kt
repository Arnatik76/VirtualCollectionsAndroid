package com.example.finalproject.models

import com.example.finalproject.models.responce.SortProperty
import com.google.gson.annotations.SerializedName

data class PageableDetails(
    @SerializedName("pageNumber")
    val pageNumber: Int,

    @SerializedName("pageSize")
    val pageSize: Int,

    @SerializedName("sort")
    val sortDetails: List<SortProperty>,

    @SerializedName("offset")
    val offset: Long,

    @SerializedName("paged")
    val paged: Boolean,

    @SerializedName("unpaged")
    val unpaged: Boolean
)
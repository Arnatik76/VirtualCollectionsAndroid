package com.example.finalproject.models.responce

import com.google.gson.annotations.SerializedName

data class PagedResponse<T>(
    @SerializedName("content")
    val content: List<T>,

    @SerializedName("pageable")
    val pageable: PageableDetails,

    @SerializedName("last")
    val last: Boolean,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalElements")
    val totalElements: Int,

    @SerializedName("size")
    val size: Int,

    @SerializedName("number")
    val number: Int,

    @SerializedName("sort")
    val sort: List<SortProperty>,

    @SerializedName("first")
    val first: Boolean,

    @SerializedName("numberOfElements")
    val numberOfElements: Int,

    @SerializedName("empty")
    val empty: Boolean
)
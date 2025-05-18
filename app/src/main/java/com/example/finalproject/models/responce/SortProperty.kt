package com.example.finalproject.models.responce

data class SortProperty(
    val direction: String?,
    val property: String?,
    val ignoreCase: Boolean?,
    val nullHandling: String?,
    val ascending: Boolean?,
    val descending: Boolean?
)

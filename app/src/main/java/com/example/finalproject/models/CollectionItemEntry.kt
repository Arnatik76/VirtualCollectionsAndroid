package com.example.finalproject.models

data class CollectionItemEntry( // Переименовал, чтобы не путать с MediaItem
    val collectionId: Int,
    val itemId: Int,
    val addedByUserId: Int?,
    val addedAt: String, // Или использовать тип Date/Timestamp
    val position: Int?,
    val notes: String?,
    val mediaItem: MediaItem, // Вложенный объект самого медиа-элемента
    val addedByUser: User? = null // Пользователь, добавивший элемент
)
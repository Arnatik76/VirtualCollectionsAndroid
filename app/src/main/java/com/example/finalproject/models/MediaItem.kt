package com.example.finalproject.models

data class MediaItem(
    val itemId: Int,
    val typeId: Int, // Может быть заменен на вложенный объект ContentType при получении
    val title: String,
    val creator: String?,
    val description: String?,
    val thumbnailUrl: String?,
    val externalUrl: String?,
    val releaseDate: String?, // Или использовать тип Date
    val addedAt: String, // Или использовать тип Date/Timestamp
    // Для удобства можно добавить поля, не являющиеся прямыми колонками,
    // например, список тегов или тип контента как объект
    val contentType: ContentType? = null, // Если API будет возвращать вложенный объект
    val tags: List<Tag>? = null // Если API будет возвращать вложенный список тегов
)
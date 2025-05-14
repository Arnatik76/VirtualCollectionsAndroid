package com.example.finalproject.models

data class Collection(
    val collectionId: Int,
    val title: String,
    val description: String?,
    val coverImageUrl: String?,
    val userId: Int, // Может быть заменен на вложенный объект User (создателя)
    val createdAt: String, // Или использовать тип Date/Timestamp
    val updatedAt: String, // Или использовать тип Date/Timestamp
    val isPublic: Boolean,
    val viewCount: Int,
    // Дополнительные поля, которые может вернуть API
    val owner: User? = null, // Если API будет возвращать вложенный объект создателя
    val items: List<CollectionItemEntry>? = null, // Список элементов в коллекции
    val collaborators: List<User>? = null, // Список соавторов
    val likeCount: Int? = null, // Количество лайков (может приходить отдельным запросом или в составе)
    val commentCount: Int? = null // Количество комментариев
)
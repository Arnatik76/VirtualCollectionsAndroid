package com.example.androidvirtualcollections

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*

object FileUtils {
    private const val TAG = "FileUtils"

    // Файлы для данных
    private const val CATALOG_FILE = "catalog.json"
    private const val COLLECTIONS_FILE = "collections.json"

    // Сохранение каталога
    fun saveCatalog(context: Context, items: List<CatalogItem>) {
        try {
            val json = Gson().toJson(items)
            context.openFileOutput(CATALOG_FILE, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            Log.d(TAG, "Каталог сохранен: ${items.size} предметов")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка сохранения каталога", e)
        }
    }

    // Загрузка каталога
    fun loadCatalog(context: Context): List<CatalogItem> {
        try {
            val file = File(context.filesDir, CATALOG_FILE)
            if (!file.exists()) {
                Log.d(TAG, "Файл каталога не найден, возвращаем пустой список")
                return emptyList()
            }

            context.openFileInput(CATALOG_FILE).use { inputStream ->
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                val json = String(buffer)
                val itemType = object : TypeToken<List<CatalogItem>>() {}.type
                val items = Gson().fromJson<List<CatalogItem>>(json, itemType)
                Log.d(TAG, "Каталог загружен: ${items.size} предметов")
                return items
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки каталога", e)
            return emptyList()
        }
    }

    // Сохранение коллекций
    fun saveCollections(context: Context, collections: List<Collection>) {
        try {
            val json = Gson().toJson(collections)
            context.openFileOutput(COLLECTIONS_FILE, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            Log.d(TAG, "Коллекции сохранены: ${collections.size} коллекций")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка сохранения коллекций", e)
        }
    }

    // Загрузка коллекций
    fun loadCollections(context: Context): List<Collection> {
        try {
            val file = File(context.filesDir, COLLECTIONS_FILE)
            if (!file.exists()) {
                Log.d(TAG, "Файл коллекций не найден, возвращаем пустой список")
                return emptyList()
            }

            context.openFileInput(COLLECTIONS_FILE).use { inputStream ->
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                val json = String(buffer)
                val collectionType = object : TypeToken<List<Collection>>() {}.type
                val collections = Gson().fromJson<List<Collection>>(json, collectionType)
                Log.d(TAG, "Коллекции загружены: ${collections.size} коллекций")
                return collections
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки коллекций", e)
            return emptyList()
        }
    }
}
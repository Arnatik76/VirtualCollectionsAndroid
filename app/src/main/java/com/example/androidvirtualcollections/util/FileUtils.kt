package com.example.androidvirtualcollections.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.androidvirtualcollections.model.Collection
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object FileUtils {
    private const val TAG = "FileUtils"
    private const val COLLECTIONS_FILE = "collections.json"

    fun saveCollections(context: Context, collections: List<Collection>) {
        try {
            val json = Gson().toJson(collections)
            context.openFileOutput(COLLECTIONS_FILE, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
            Log.d(TAG, "Коллекции сохранены: ${collections.size}")
            Toast.makeText(context, "Коллекции сохранены", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка сохранения коллекций", e)
            Toast.makeText(context, "Ошибка сохранения коллекций", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadCollections(context: Context): List<Collection> {
        try {
            val file = File(context.filesDir, COLLECTIONS_FILE)
            if (!file.exists()) {
                return emptyList()
            }

            context.openFileInput(COLLECTIONS_FILE).use { inputStream ->
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                val json = String(buffer)
                val type = object : TypeToken<List<Collection>>() {}.type
                val loadedCollections = Gson().fromJson<List<Collection>>(json, type) ?: emptyList()

                return loadedCollections.map { collection ->
                    collection.copy(items = collection.items ?: mutableListOf())
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки коллекций", e)
            return emptyList()
        }
    }
}
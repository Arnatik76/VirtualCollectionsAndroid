package com.example.androidvirtualcollections

import android.content.Context
import android.util.Log
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
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка сохранения коллекций", e)
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
                return Gson().fromJson(json, type) ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки коллекций", e)
            return emptyList()
        }
    }
}
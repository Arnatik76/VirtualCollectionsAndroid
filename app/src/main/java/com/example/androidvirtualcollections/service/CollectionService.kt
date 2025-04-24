package com.example.androidvirtualcollections.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.androidvirtualcollections.util.FileUtils
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class CollectionService : Service() {

    companion object {
        private const val TAG = "CollectionService"

        const val ACTION_BACKUP_COLLECTIONS = "action_backup_collections"
        const val ACTION_EXPORT_COLLECTIONS = "action_export_collections"

        private const val BACKUP_DIR = "backups"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            ACTION_BACKUP_COLLECTIONS -> {
                thread {
                    backupCollections()
                    stopSelf(startId)
                }
            }
            ACTION_EXPORT_COLLECTIONS -> {
                thread {
                    stopSelf(startId)
                }
            }
        }

        return START_NOT_STICKY
    }

    private fun backupCollections() {
        try {
            val collections = FileUtils.loadCollections(this)

            val backupDir = File(filesDir, BACKUP_DIR)
            if (!backupDir.exists()) {
                backupDir.mkdir()
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val backupFile = File(backupDir, "collections_backup_$timestamp.json")

            val json = Gson().toJson(collections)
            FileOutputStream(backupFile).use { it.write(json.toByteArray()) }

            Log.d(TAG, "Резервная копия создана: ${backupFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка создания резервной копии", e)
        }
    }
}
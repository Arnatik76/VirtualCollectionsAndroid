package com.example.finalproject.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.finalproject.models.User
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

object AuthTokenProvider {

    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_CURRENT_USER = "current_user"

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String?) {
        sharedPreferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUser(user: User?) {
        val userJson = if (user != null) gson.toJson(user) else null
        sharedPreferences.edit().putString(KEY_CURRENT_USER, userJson).apply()
    }

    fun getCurrentUser(): User? {
        val userJson = sharedPreferences.getString(KEY_CURRENT_USER, null)
        return if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: JsonSyntaxException) {
                clearAuthData()
                null
            }
        } else {
            null
        }
    }

    fun clearAuthData() {
        sharedPreferences.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_CURRENT_USER)
            .apply()
    }

    fun isAuthenticated(): Boolean {
        return getToken() != null && getCurrentUser() != null
    }
}
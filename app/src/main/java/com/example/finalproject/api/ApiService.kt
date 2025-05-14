package com.example.finalproject.api

import com.example.finalproject.models.Collection
import com.example.finalproject.models.LoginRequest
import com.example.finalproject.models.AuthResponse
import com.example.finalproject.models.ForgotPasswordRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("collections")
    fun createCollection(
        @Body collection: Collection
    ): Call<Collection>

    @GET("collections/{id}")
    fun getCollections(@Path("id") collectionId: Int): Call<Collection>

    // Добавлен эндпоинт для входа
    @POST("auth/login") // Убедитесь, что путь "auth/login" соответствует вашему API
    fun loginUser(@Body loginRequest: LoginRequest): Call<AuthResponse>

    @POST("auth/forgot-password")
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ResponseBody>

//    @GET("achievements/{id}")
//    fun getAchievement(@Path("id") achievementId: Int): Call<AchievementType>
}
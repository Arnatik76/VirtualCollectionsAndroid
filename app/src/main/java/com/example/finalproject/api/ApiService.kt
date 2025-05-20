package com.example.finalproject.api

import com.example.finalproject.models.Collection
import com.example.finalproject.models.request.LoginRequest
import com.example.finalproject.models.responce.AuthResponse
import com.example.finalproject.models.CollectionComment
import com.example.finalproject.models.CollectionItemEntry
import com.example.finalproject.models.request.ForgotPasswordRequest
import com.example.finalproject.models.responce.PagedResponse
import com.example.finalproject.models.request.RegistrationRequest
import com.example.finalproject.models.CollectionCollaborator
import com.example.finalproject.models.request.CreateCollectionRequest
import com.example.finalproject.models.request.CreateCommentRequest
import com.example.finalproject.models.request.AddItemToCollectionRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("vc-collections/collections")
    fun createCollection(
        @Body createCollectionRequest: CreateCollectionRequest
    ): Call<Collection>

    // @PUT("vc-collections/collections/{id}")
    // fun updateCollection(
    //     @Path("id") collectionId: Int,
    //     @Body updateCollectionRequest: CreateCollectionRequest
    // ): Call<Collection>

    @GET("vc-collections/collections/{id}")
    fun getCollectionById(@Path("id") collectionId: Int): Call<Collection>

    @GET("vc-collections/collections/public-feed")
    fun getPublicCollections(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "viewCount,desc"
    ): Call<PagedResponse<Collection>>

    @GET("vc-collections/collections/my")
    fun getMyCollections(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "updatedAt,desc"
    ): Call<PagedResponse<Collection>>

    @GET("collections/{collectionId}/items")
    fun getCollectionItems(
        @Path("collectionId") collectionId: Int,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Call<PagedResponse<CollectionItemEntry>>

    @POST("vc-collections/collections/{collectionId}/items")
    fun addItemToCollection(
        @Path("collectionId") collectionId: Int,
        @Body addItemRequest: AddItemToCollectionRequest
    ): Call<CollectionItemEntry>

    @GET("collections/{collectionId}/comments")
    fun getCollectionComments(
        @Path("collectionId") collectionId: Int,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Call<PagedResponse<CollectionComment>>

    @GET("collections/{collectionId}/collaborators")
    fun getCollectionCollaborators(@Path("collectionId") collectionId: Int): Call<List<CollectionCollaborator>>

    @POST("collections/{collectionId}/like")
    fun likeCollection(@Path("collectionId") collectionId: Int): Call<ResponseBody>

    @DELETE("collections/{collectionId}/like")
    fun unlikeCollection(@Path("collectionId") collectionId: Int): Call<ResponseBody>

    @POST("collections/{collectionId}/comments")
    fun addCollectionComment(
        @Path("collectionId") collectionId: Int,
        @Body commentRequest: CreateCommentRequest
    ): Call<CollectionComment>

    @POST("vc-users/auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<AuthResponse>

    @POST("vc-users/auth/forgot-password")
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ResponseBody>

    @POST("vc-users/auth/register")
    fun registerUser(@Body registrationRequest: RegistrationRequest): Call<AuthResponse>
}
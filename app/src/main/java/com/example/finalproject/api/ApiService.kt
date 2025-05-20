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
import com.example.finalproject.models.MediaItem
import com.example.finalproject.models.User
import com.example.finalproject.models.request.CreateCollectionRequest
import com.example.finalproject.models.request.CreateCommentRequest
import com.example.finalproject.models.request.AddItemToCollectionRequest
import com.example.finalproject.models.request.UpdateProfileRequest
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
    fun getCollectionById(@Path("id") collectionId: Long): Call<Collection>

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

    @GET("vc-collections/collections/{collectionId}/items")
    fun getCollectionItems(
        @Path("collectionId") collectionId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Call<PagedResponse<CollectionItemEntry>>

    @POST("vc-collections/collections/{collectionId}/items")
    fun addItemToCollection(
        @Path("collectionId") collectionId: Long,
        @Body addItemRequest: AddItemToCollectionRequest
    ): Call<CollectionItemEntry>

    @GET("vc-collections/collections/{collectionId}/comments")
    fun getCollectionComments(
        @Path("collectionId") collectionId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Call<PagedResponse<CollectionComment>>

    @GET("vc-collections/collections/{collectionId}/collaborators")
    fun getCollectionCollaborators(@Path("collectionId") collectionId: Long): Call<List<CollectionCollaborator>>

    @POST("vc-collections/collections/{collectionId}/like")
    fun likeCollection(@Path("collectionId") collectionId: Long): Call<ResponseBody>

    @DELETE("vc-collections/collections/{collectionId}/like")
    fun unlikeCollection(@Path("collectionId") collectionId: Long): Call<ResponseBody>

    @POST("vc-collections/collections/{collectionId}/comments")
    fun addCollectionComment(
        @Path("collectionId") collectionId: Long,
        @Body commentRequest: CreateCommentRequest
    ): Call<CollectionComment>

    @GET("media-items/search")
    fun searchMediaItems(
        @Query("query") query: String,
        @Query("type") type: String? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 15
    ): Call<PagedResponse<MediaItem>>



    @POST("vc-users/auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<AuthResponse>

    @POST("vc-users/auth/forgot-password")
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ResponseBody>

    @POST("vc-users/auth/register")
    fun registerUser(@Body registrationRequest: RegistrationRequest): Call<AuthResponse>

    @GET("vc-users/users/{userId}")
    fun getUserProfileById(@Path("userId") userId: Long): Call<User>

    @PUT("vc-users/users/me")
    fun updateMyProfile(@Body updateProfileRequest: UpdateProfileRequest): Call<User>
}
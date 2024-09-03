package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterContent
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterListResponse
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterSearchResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface BeforeAfterService {
    @GET("/bite/before-after")
    suspend fun retrieveBeforeAfterList() : Response<List<BeforeAfterListResponse>>

    @GET("/bite/before-after/{beforeAfterId}")
    suspend fun retrieveBeforeAfterContent(@Path("beforeAfterId") beforeAfterId: Long) : Response<BeforeAfterContent>

    @Multipart
    @POST("/bite/before-after")
    suspend fun makeBeforeAfter(
        @Part("beforeAfterRequest") beforeAfterRequest: RequestBody,
        @Part beforeThumbnail: MultipartBody.Part,
        @Part afterThumbnail: MultipartBody.Part,
        @Part beforeContent: MultipartBody.Part,
        @Part afterContent: MultipartBody.Part,
    ): Call<String>

    @GET("/bite/before-after/search/title")
    suspend fun searchBeforeAfterByTitle(@Query("keyword") keyword: String) : Response<List<BeforeAfterSearchResponse>>

    @GET("/bite/before-after/search/hashtag")
    suspend fun searchBeforeAfterByHashtag(@Query("tag") hashtags: List<String>) : Response<List<BeforeAfterSearchResponse>>
}
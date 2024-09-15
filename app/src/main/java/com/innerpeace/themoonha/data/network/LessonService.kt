package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.lesson.*
import retrofit2.http.*

interface LessonService {
    @GET("lesson/list")
    suspend fun getLessonList(@QueryMap lessonListRequestMap: Map<String, String>): LessonListResponse

    @GET("lesson/detail/{lessonId}")
    suspend fun getLessonDetail(@Path("lessonId") lessonId: Long): LessonDetailResponse

    @GET("lesson/cart")
    suspend fun getLessonCart(): List<CartResponse>

    @POST("lesson/cart")
    suspend fun addLessonCart(@Body cartRequest: CartRequest): CommonResponse

    @POST("lesson/pay")
    suspend fun payLesson(@Body sugangRequest: SugangRequest): CommonResponse

    @GET("lesson/enroll")
    suspend fun getLessonListByMember(): List<LessonEnrollResponse>

    @GET("lesson/shortform/{shortFormId}")
    suspend fun getShortFormDetail(@Path("shortFormId") shortFormId: Long)

    @GET("lesson/field/enroll")
    suspend fun getLessonFieldListByMember(): List<LessonEnrollResponse>

}
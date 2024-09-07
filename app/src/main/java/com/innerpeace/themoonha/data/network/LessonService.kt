package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.lesson.CartRequest
import com.innerpeace.themoonha.data.model.lesson.CartResponse
import com.innerpeace.themoonha.data.model.lesson.LessonDetailResponse
import com.innerpeace.themoonha.data.model.lesson.LessonEnrollResponse
import com.innerpeace.themoonha.data.model.lesson.LessonListResponse
import com.innerpeace.themoonha.data.model.lesson.SugangRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

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
}
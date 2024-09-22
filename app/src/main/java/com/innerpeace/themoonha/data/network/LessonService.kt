package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.lesson.CartRequest
import com.innerpeace.themoonha.data.model.lesson.CartResponse
import com.innerpeace.themoonha.data.model.lesson.LessonDetailResponse
import com.innerpeace.themoonha.data.model.lesson.LessonEnrollResponse
import com.innerpeace.themoonha.data.model.lesson.LessonListResponse
import com.innerpeace.themoonha.data.model.lesson.SugangRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

/**
 * 강좌 도메인 서비스
 *
 * @author 손승완
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31 	손승완       최초 생성
 * </pre>
 * @since 2024.08.31
 */
interface LessonService {
    @GET("lesson/list")
    suspend fun getLessonList(@QueryMap lessonListRequestMap: Map<String, String>): LessonListResponse

    @GET("lesson/detail/{lessonId}")
    suspend fun getLessonDetail(@Path("lessonId") lessonId: Long): LessonDetailResponse

    @GET("lesson/cart")
    suspend fun getLessonCart(): List<CartResponse>

    @POST("lesson/cart")
    suspend fun addLessonCart(@Body cartRequest: CartRequest): CommonResponse

    @DELETE("lesson/cart/{cartId}")
    suspend fun removeCart(@Path("cartId") cartId: Long): CommonResponse

    @POST("lesson/pay")
    suspend fun payLesson(@Body sugangRequest: SugangRequest): CommonResponse

    @GET("lesson/enroll")
    suspend fun getLessonListByMember(): List<LessonEnrollResponse>

    @GET("lesson/shortform/{shortFormId}")
    suspend fun getShortFormDetail(@Path("shortFormId") shortFormId: Long)

    @GET("lesson/field/enroll")
    suspend fun getLessonFieldListByMember(): List<LessonEnrollResponse>
}
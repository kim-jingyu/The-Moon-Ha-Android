package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.live.LiveLessonDetailResponse
import com.innerpeace.themoonha.data.model.live.LiveLessonResponse
import com.innerpeace.themoonha.data.model.live.LiveLessonStatusResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * 실시간 강좌 API 서비스
 * @author 김진규
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	김진규       최초 생성
 * </pre>
 */
interface LiveService {
    @GET("/live/enrolled/by-latest")
    suspend fun retrieveLiveLessonListWithMember(): Response<List<LiveLessonResponse>>

    @GET("/live/enrolled/by-title")
    suspend fun retrieveLiveLessonListWithMemberOrderByTitle(): Response<List<LiveLessonResponse>>

    @GET("/live/not-enrolled/by-latest")
    suspend fun retrieveLiveLessonListWithoutMember(): Response<List<LiveLessonResponse>>

    @GET("/live/not-enrolled/by-title")
    suspend fun retrieveLiveLessonListWithoutMemberOrderByTitle(): Response<List<LiveLessonResponse>>

    @GET("/live/{liveId}")
    suspend fun retrieveLiveLessonDetail(@Path("liveId") liveId: Long): Response<LiveLessonDetailResponse>

    @Multipart
    @POST("/live")
    suspend fun makeLiveLesson(
        @Part("liveLessonRequest") liveLessonRequest: RequestBody,
        @Part thumbnail: MultipartBody.Part
    ): CommonResponse

    @POST("/live/{liveId}/end")
    suspend fun endLiveLesson(@Path("liveId") liveId: Long): CommonResponse

    @GET("/live/{liveId}/status")
    suspend fun retrieveLiveLessonStatus(@Path("liveId") liveId: Long): Response<LiveLessonStatusResponse>

    @GET("/live/{liveId}/viewers")
    suspend fun getViewersCount(@Path("liveId") liveId: Long): Response<Int>

    @GET("/live/{liveId}/likes")
    suspend fun getLLikesCount(@Path("liveId") liveId: Long): Response<Int>

    @POST("/live/{liveId}/join")
    suspend fun joinLiveLesson(@Path("liveId") liveId: Long): Response<Int>

    @POST("/{liveId}/leave")
    suspend fun leaveLiveLesson(@Path("liveId") liveId: Long): Response<Int>

    @POST("/live/{liveId}/like")
    suspend fun likeLiveLesson(@Path("liveId") liveId: Long): Response<Int>

    @GET("/live/{liveId}/share")
    suspend fun getShareLink(@Path("liveId") liveId: Long): Response<String>
}
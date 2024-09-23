package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.lounge.Attendance
import com.innerpeace.themoonha.data.model.lounge.LoungeCommentRequest
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
import com.innerpeace.themoonha.data.model.lounge.LoungeListResponse
import com.innerpeace.themoonha.data.model.lounge.LoungePostListResponse
import com.innerpeace.themoonha.data.model.lounge.LoungePostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 라운지 서비스
 * @author 조희정
 * @since 2024.08.30
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.30  	조희정       최초 생성
 * 2024.08.30   조희정       라운지 목록 불러오기 구현
 * 2024.09.02   조희정       라운지 게시글 상세보기 구현
 * 2024.09.09   조희정       출석 시작, 출석 상태 수정 구현
 * 2024.09.09   조희정       출석 현황 보기 구현
 * </pre>
 */
interface LoungeService {

    // 라운지 목록
    @GET("lounge/list")
    suspend fun getLoungeList(): List<LoungeListResponse>

    // 라운지 홈
    @GET("lounge/{loungeId}/home")
    suspend fun getLoungeHome(@Path("loungeId") loungeId: Long): LoungeHomeResponse

    // 라운지 게시글 리스트
    @GET("/lounge/{loungeId}/posts")
    suspend fun getLoungePostList(@Path("loungeId") loungeId: Long,
                                  @Query("page") page: Int,
                                  @Query("size") size: Int): List<LoungePostListResponse>

    // 게시글 상세
    @GET("lounge/{loungeId}/post/{loungePostId}")
    suspend fun getPostDetail(@Path("loungeId") loungeId: Long, @Path("loungePostId") loungePostId: Long): LoungePostResponse

    // 게시글 저장
    @Multipart
    @POST("lounge/post/register")
    suspend fun registerLoungePost(
        @Part("loungePostRequest") loungePostRequest: RequestBody,
        @Part files: List<MultipartBody.Part>?
    ): Response<CommonResponse>

    // 댓글 저장
    @POST("lounge/comment/register")
    suspend fun registerComment(
        @Body loungeCommentRequest: LoungeCommentRequest): Response<CommonResponse>

    // 출석 시작
    @POST("lounge/attendance/{lessonId}")
    suspend fun startAttendance(@Path("lessonId") lessonId: Long): List<Attendance>

    // 출석 수정
    @POST("lounge/attendance/update/{attendanceId}")
    suspend fun updateAttendanceStatus(@Path("attendanceId") attendanceId: Long): Response<CommonResponse>

}
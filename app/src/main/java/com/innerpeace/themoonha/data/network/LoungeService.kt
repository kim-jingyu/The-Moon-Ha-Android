package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
import com.innerpeace.themoonha.data.model.lounge.LoungeListResponse
import com.innerpeace.themoonha.data.model.lounge.LoungePostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

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
 * </pre>
 */
interface LoungeService {

    @GET("lounge/list")
    suspend fun getLoungeList(): List<LoungeListResponse>

    @GET("lounge/{loungeId}/home")
    suspend fun getLoungeHome(@Path("loungeId") loungeId: Long): LoungeHomeResponse

    @GET("lounge/{loungeId}/post/{loungePostId}")
    suspend fun getPostDetail(@Path("loungeId") loungeId: Long, @Path("loungePostId") loungePostId: Long): LoungePostResponse

    @Multipart
    @POST("lounge/post/register")
    suspend fun registerLoungePost(
        @Part("loungePostRequest") loungePostRequest: RequestBody,
        @Part files: List<MultipartBody.Part>?
    ): Response<CommonResponse>
}
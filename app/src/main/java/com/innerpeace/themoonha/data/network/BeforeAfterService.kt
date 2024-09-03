package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterDetailResponse
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterListResponse
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterSearchResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * 비포애프터 API 서비스
 * @author 김진규
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  	김진규       최초 생성
 * </pre>
 */
interface BeforeAfterService {
    @GET("/bite/before-after")
    suspend fun retrieveBeforeAfterList() : Response<List<BeforeAfterListResponse>>

    @GET("/bite/before-after/{beforeAfterId}")
    suspend fun retrieveBeforeAfterContent(@Path("beforeAfterId") beforeAfterId: Long) : Response<BeforeAfterDetailResponse>

    @Multipart
    @POST("/bite/before-after")
    suspend fun makeBeforeAfter(
        @Part("beforeAfterRequest") beforeAfterRequest: RequestBody,
        @Part beforeThumbnail: MultipartBody.Part,
        @Part afterThumbnail: MultipartBody.Part,
        @Part beforeContent: MultipartBody.Part,
        @Part afterContent: MultipartBody.Part,
    ): CommonResponse

    @GET("/bite/before-after/search/title")
    suspend fun searchBeforeAfterByTitle(@Query("keyword") keyword: String) : Response<List<BeforeAfterSearchResponse>>

    @GET("/bite/before-after/search/hashtag")
    suspend fun searchBeforeAfterByHashtag(@Query("tag") hashtags: List<String>) : Response<List<BeforeAfterSearchResponse>>
}
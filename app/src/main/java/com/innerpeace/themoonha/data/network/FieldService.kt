package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.field.FieldDetailResponse
import com.innerpeace.themoonha.data.model.field.FieldListResponse
import com.innerpeace.themoonha.data.model.field.FieldSearchResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 분야별 한 입 API 서비스
 * @author 김진규
 * @since 2024.09.04
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.04  	김진규       최초 생성
 * </pre>
 */
interface FieldService {
    @GET("/bite/field/by-latest")
    suspend fun retrieveFieldList() : Response<List<FieldListResponse>>

    @GET("/bite/field/by-title")
    suspend fun retrieveFieldListOrderByTitle() : Response<List<FieldListResponse>>

    @GET("/bite/field/by-category/{categoryId}")
    fun retrieveFieldListByCategory(@Path("categoryId") categoryId: Long): Response<List<FieldListResponse>>

    @GET("/bite/field/{fieldId}")
    suspend fun retrieveFieldContent(@Path("fieldId") fieldId: Long) : Response<FieldDetailResponse>

    @GET("/bite/field/details/by-latest")
    suspend fun retrieveFieldContentsByLatest() : Response<List<FieldDetailResponse>>

    @GET("/bite/field/details/by-title")
    suspend fun retrieveFieldContentsByTitle() : Response<List<FieldDetailResponse>>

    @Multipart
    @POST("/bite/field")
    suspend fun makeField(
        @Part("fieldRequest") fieldRequest: RequestBody,
        @Part thumbnail: MultipartBody.Part,
        @Part content: MultipartBody.Part,
    ): CommonResponse

    @GET("/bite/field/search/title")
    suspend fun searchFieldByTitle(@Query("keyword") keyword: String) : Response<List<FieldSearchResponse>>

    @GET("/bite/field/search/hashtag")
    suspend fun searchFieldByHashtag(@Query("tag") hashtags: List<String>) : Response<List<FieldSearchResponse>>
}
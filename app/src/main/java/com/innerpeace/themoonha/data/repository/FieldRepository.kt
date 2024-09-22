package com.innerpeace.themoonha.data.repository

import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.field.FieldDetailResponse
import com.innerpeace.themoonha.data.model.field.FieldListResponse
import com.innerpeace.themoonha.data.model.field.FieldSearchResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.FieldService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

/**
 * 분야별 한 입 API 서비스 구현체
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
class FieldRepository: FieldService {
    private val api: FieldService get() = ApiClient.getClient().create(FieldService::class.java)

    override suspend fun retrieveFieldList(): Response<List<FieldListResponse>> {
        return api.retrieveFieldList()
    }

    override suspend fun retrieveFieldListOrderByTitle(): Response<List<FieldListResponse>> {
        return api.retrieveFieldListOrderByTitle()
    }

    override suspend fun retrieveFieldListByCategory(categoryId: Long): Response<List<FieldListResponse>> {
        return api.retrieveFieldListByCategory(categoryId)
    }

    override suspend fun retrieveFieldContent(fieldId: Long): Response<FieldDetailResponse> {
        return api.retrieveFieldContent(fieldId)
    }

    override suspend fun retrieveFieldContentsByLatest(): Response<List<FieldDetailResponse>> {
        return api.retrieveFieldContentsByLatest()
    }

    override suspend fun retrieveFieldContentsByTitle(): Response<List<FieldDetailResponse>> {
        return api.retrieveFieldContentsByTitle()
    }

    override suspend fun makeField(
        fieldRequest: RequestBody,
        thumbnail: MultipartBody.Part,
        content: MultipartBody.Part
    ): CommonResponse {
        return api.makeField(
            fieldRequest = fieldRequest,
            thumbnail = thumbnail,
            content = content
        )
    }

    override suspend fun searchFieldByTitle(keyword: String): Response<List<FieldSearchResponse>> {
        return api.searchFieldByTitle(keyword)
    }

    override suspend fun searchFieldByHashtag(hashtags: List<String>): Response<List<FieldSearchResponse>> {
        return api.searchFieldByHashtag(hashtags)
    }
}

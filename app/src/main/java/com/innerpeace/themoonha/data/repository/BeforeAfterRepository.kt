package com.innerpeace.themoonha.data.repository

import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterDetailResponse
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterListResponse
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterSearchResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.BeforeAfterService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

/**
 * 비포애프터 API 서비스 구현체
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
class BeforeAfterRepository: BeforeAfterService {
    private val api: BeforeAfterService get() = ApiClient.getClient().create(BeforeAfterService::class.java)

    override suspend fun retrieveBeforeAfterList(): Response<List<BeforeAfterListResponse>> {
        return api.retrieveBeforeAfterList()
    }

    override suspend fun retrieveBeforeAfterListOrderByTitle(): Response<List<BeforeAfterListResponse>> {
        return api.retrieveBeforeAfterListOrderByTitle()
    }

    override suspend fun retrieveBeforeAfterContent(beforeAfterId: Long): Response<BeforeAfterDetailResponse> {
        return api.retrieveBeforeAfterContent(beforeAfterId)
    }

    override suspend fun retrieveBeforeAfterContentsByLatest(): Response<List<BeforeAfterDetailResponse>> {
        return api.retrieveBeforeAfterContentsByLatest()
    }

    override suspend fun retrieveBeforeAfterContentsByTitle(): Response<List<BeforeAfterDetailResponse>> {
        return api.retrieveBeforeAfterContentsByTitle()
    }

    override suspend fun makeBeforeAfter(
        beforeAfterRequest: RequestBody,
        beforeThumbnail: MultipartBody.Part,
        afterThumbnail: MultipartBody.Part,
        beforeContent: MultipartBody.Part,
        afterContent: MultipartBody.Part
    ): CommonResponse {
        return api.makeBeforeAfter(
            beforeAfterRequest = beforeAfterRequest,
            beforeThumbnail = beforeThumbnail,
            afterThumbnail = afterThumbnail,
            beforeContent = beforeContent,
            afterContent = afterContent
        )
    }

    override suspend fun searchBeforeAfterByTitle(keyword: String): Response<List<BeforeAfterSearchResponse>> {
        return api.searchBeforeAfterByTitle(keyword)
    }

    override suspend fun searchBeforeAfterByHashtag(hashtags: List<String>): Response<List<BeforeAfterSearchResponse>> {
        return api.searchBeforeAfterByHashtag(hashtags)
    }
}

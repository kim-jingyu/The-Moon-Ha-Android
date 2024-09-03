package com.innerpeace.themoonha.data.repository

import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterContent
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterListResponse
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterSearchResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.BeforeAfterService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

class BeforeAfterRepository: BeforeAfterService {
    private val api: BeforeAfterService get() = ApiClient.getClient().create(BeforeAfterService::class.java)

    override suspend fun retrieveBeforeAfterList(): Response<List<BeforeAfterListResponse>> {
        return api.retrieveBeforeAfterList()
    }

    override suspend fun retrieveBeforeAfterContent(beforeAfterId: Long): Response<BeforeAfterContent> {
        return api.retrieveBeforeAfterContent(beforeAfterId)
    }

    override suspend fun makeBeforeAfter(
        beforeAfterRequest: RequestBody,
        beforeThumbnail: MultipartBody.Part,
        afterThumbnail: MultipartBody.Part,
        beforeContent: MultipartBody.Part,
        afterContent: MultipartBody.Part
    ): Call<String> {
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

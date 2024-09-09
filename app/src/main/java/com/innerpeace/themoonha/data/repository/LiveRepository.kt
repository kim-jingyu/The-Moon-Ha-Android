package com.innerpeace.themoonha.data.repository

import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.live.LiveLessonDetailResponse
import com.innerpeace.themoonha.data.model.live.LiveLessonResponse
import com.innerpeace.themoonha.data.model.live.LiveLessonStatusResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LiveService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class LiveRepository: LiveService {
    private val api: LiveService get() = ApiClient.getClient().create(LiveService::class.java)

    override suspend fun retrieveLiveLessonListWithMember(): Response<List<LiveLessonResponse>> {
        return api.retrieveLiveLessonListWithMember()
    }

    override suspend fun retrieveLiveLessonListWithMemberOrderByTitle(): Response<List<LiveLessonResponse>> {
        return api.retrieveLiveLessonListWithMemberOrderByTitle()
    }

    override suspend fun retrieveLiveLessonListWithoutMember(): Response<List<LiveLessonResponse>> {
        return api.retrieveLiveLessonListWithoutMember()
    }

    override suspend fun retrieveLiveLessonListWithoutMemberOrderByTitle(): Response<List<LiveLessonResponse>> {
        return api.retrieveLiveLessonListWithoutMemberOrderByTitle()
    }

    override suspend fun retrieveLiveLessonDetail(liveId: Long): Response<LiveLessonDetailResponse> {
        return api.retrieveLiveLessonDetail(liveId)
    }

    override suspend fun makeLiveLesson(
        liveLessonRequest: RequestBody,
        thumbnail: MultipartBody.Part
    ): CommonResponse {
        return api.makeLiveLesson(
            liveLessonRequest = liveLessonRequest,
            thumbnail = thumbnail
        )
    }

    override suspend fun endLiveLesson(liveId: Long): CommonResponse {
        return api.endLiveLesson(liveId)
    }

    override suspend fun retrieveLiveLessonStatus(liveId: Long): Response<LiveLessonStatusResponse> {
        return api.retrieveLiveLessonStatus(liveId)
    }

    override suspend fun getViewersCount(liveId: Long): Response<Int> {
        return api.getViewersCount(liveId)
    }

    override suspend fun getLLikesCount(liveId: Long): Response<Int> {
        return api.getLLikesCount(liveId)
    }

    override suspend fun joinLiveLesson(liveId: Long): Response<Int> {
        return api.joinLiveLesson(liveId)
    }

    override suspend fun leaveLiveLesson(liveId: Long): Response<Int> {
        return api.leaveLiveLesson(liveId)
    }

    override suspend fun likeLiveLesson(liveId: Long): Response<Int> {
        return api.likeLiveLesson(liveId)
    }

    override suspend fun getShareLink(liveId: Long): Response<String> {
        return api.getShareLink(liveId)
    }
}
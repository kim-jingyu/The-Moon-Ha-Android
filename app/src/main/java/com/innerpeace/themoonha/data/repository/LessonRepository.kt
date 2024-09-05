package com.innerpeace.themoonha.data.repository

import android.util.Log
import com.innerpeace.themoonha.data.model.lesson.LessonDetailResponse
import com.innerpeace.themoonha.data.model.lesson.LessonEnrollResponse
import com.innerpeace.themoonha.data.model.lesson.LessonListResponse
import com.innerpeace.themoonha.data.network.LessonService

class LessonRepository(private val lessonService: LessonService) {

    suspend fun fetchLessonList(lessonListRequestMap: Map<String, String>): LessonListResponse? {
        return try {
            lessonService.getLessonList(lessonListRequestMap)
        } catch (e: Exception) {
            Log.e("강좌 목록 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchLessonDetail(lessonId: Long): LessonDetailResponse? {
        return try {
            lessonService.getLessonDetail(lessonId)
        } catch (e: Exception) {
            Log.e("강좌 상세 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    suspend fun fetchLessonEnroll(): List<LessonEnrollResponse>? {
        return try {
            lessonService.getLessonListByMember()
        } catch (e: Exception) {
            Log.e("회원별 강좌 목록 조회 응답 실패", "${e.message}", e)
            null
        }
    }
}
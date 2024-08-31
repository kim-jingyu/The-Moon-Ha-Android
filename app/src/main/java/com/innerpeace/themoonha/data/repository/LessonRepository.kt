package com.innerpeace.themoonha.data.repository

import android.util.Log
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
}
package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.lesson.LessonListResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface LessonService {
    @GET("lesson/list")
    suspend fun getLessonList(@QueryMap lessonListRequestMap: Map<String, String>): LessonListResponse
}
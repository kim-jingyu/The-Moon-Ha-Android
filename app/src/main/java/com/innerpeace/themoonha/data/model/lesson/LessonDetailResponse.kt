package com.innerpeace.themoonha.data.model.lesson

data class LessonDetailResponse(
    val lessonId: Long,
    val title: String,
    val branchName: String,
    val period: String,
    val lessonTime: String,
    val cnt: Int,
    val place: String,
    val tutorName: String,
    val cost: Int,
    val summary: String,
    val curriculum: String,
    val supply: String,
    val thumbnailUrl: String,
    val previewVideoUrl: String,
    val tutorId: Int,
    val tutorProfileImgUrl: String
)

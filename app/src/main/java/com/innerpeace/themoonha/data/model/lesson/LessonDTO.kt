package com.innerpeace.themoonha.data.model.lesson

data class LessonDTO(
    val lessonId: Long,
    val thumbnailUrl: String,
    val target: Int,
    val title: String,
    val cnt: Int,
    val cost: Int,
    val tutorName: String,
    val lessonTime: String,
    val endDate: String
) {
    fun getTargetDescription(): String {
        return TargetType.fromId(target)?.description ?: "전체"
    }
}

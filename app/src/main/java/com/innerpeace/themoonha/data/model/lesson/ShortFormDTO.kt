package com.innerpeace.themoonha.data.model.lesson

data class ShortFormDTO(
    val lessonId: Long,
    val shortFormId: Long,
    val categoryId: Long,
    val lessonTitle: String,
    val tutorName: String,
    val shortFormName: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val target: Int

) {
    fun getTargetDescription(): String {
        return TargetType.fromId(target)?.description ?: "전체"
    }
}


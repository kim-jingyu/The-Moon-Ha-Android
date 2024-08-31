package com.innerpeace.themoonha.data.model.lesson

data class LessonListResponse(
    val branchId: Long,
    val shortFormList: List<ShortFormDTO>,
    val memberName: String,
    val lessonList: List<LessonDTO>
)

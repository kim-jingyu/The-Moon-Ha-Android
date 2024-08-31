package com.innerpeace.themoonha.data.model.lesson

data class LessonListRequest(
    val branchId: String?,
    val lessonTitle: String?,
    val tutorName: String?,
    val day: String?,
    val target: Int?,
    val categoryId: Int?,
    val cnt: Int?,
    val lessonTime: Int?
)

fun LessonListRequest.toQueryMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    branchId?.let { map["branchId"] = it.toString() }
    lessonTitle?.let { map["lessonTitle"] = it }
    tutorName?.let { map["tutorName"] = it }
    day?.let { map["day"] = it }
    target?.let { map["target"] = it.toString() }
    categoryId?.let { map["categoryId"] = it.toString() }
    cnt?.let { map["cnt"] = it.toString() }
    lessonTime?.let { map["lessonTime"] = it.toString() }
    return map
}

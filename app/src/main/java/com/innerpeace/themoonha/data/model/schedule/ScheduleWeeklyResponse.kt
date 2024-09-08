package com.innerpeace.themoonha.data.model.schedule

data class ScheduleWeeklyResponse(
    val lessonId: Long,
    val day: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val title: String,
    val standardDate: String
)
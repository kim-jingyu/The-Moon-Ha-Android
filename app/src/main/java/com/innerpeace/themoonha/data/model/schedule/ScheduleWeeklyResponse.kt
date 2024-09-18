package com.innerpeace.themoonha.data.model.schedule

import com.google.android.exoplayer2.source.dash.manifest.Period

data class ScheduleWeeklyResponse(
    val lessonId: Long,
    val day: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val title: String,
    val standardDate: String,
    val branchName: String,
    val cnt: Int,
    val period: String,
    val lessonTime: String,
    val tutorName: String,
    val loungeId: Long
)
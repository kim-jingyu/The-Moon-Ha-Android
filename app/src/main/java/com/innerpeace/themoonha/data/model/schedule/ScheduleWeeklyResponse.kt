package com.innerpeace.themoonha.data.model.schedule


/**
 * 주간 스케줄 응답 DTO
 * @author 조희정
 * @since 2024.09.09
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.09  	조희정       최초 생성
 * </pre>
 */
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
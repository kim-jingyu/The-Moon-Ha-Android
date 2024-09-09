package com.innerpeace.themoonha.data.model.schedule

/**
 * 다음 스케줄 응답 DTO
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
data class ScheduleNextResponse (
    val lessonId: Long,
    val branchName: String,
    val lessonTitle: String,
    val cnt: Int,
    val period: String,
    val lessonTime: String,
    val tutorName: String,
    val target: String,
    val loungeId: Long,
    val onlineYn: Boolean
)
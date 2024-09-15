package com.innerpeace.themoonha.data.model.lounge

/**
 * 라운지 출석 현황 Response
 * @author 조희정
 * @since 2024.09.10
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.10  	조희정       최초 생성
 * </pre>
 */
data class AttendanceMembersResponse(
    val attendanceDates: List<String>,
    val students: List<AttendanceMembers>
) {
    data class AttendanceMembers(
        val name: String,
        val attendanceCnt: Int,
        val attendance: List<Boolean>
    )
}
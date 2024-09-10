package com.innerpeace.themoonha.data.model.lounge

/**
 * 라운지 출석용 Response
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
data class Attendance (
    val attendanceId: Long,
    val memberId: Long,
    val name: String,
    val profileImgUrl: String,
    val attendanceDate: String,
    var attendanceYn: Boolean
)
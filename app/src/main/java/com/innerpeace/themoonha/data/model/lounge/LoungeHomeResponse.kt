package com.innerpeace.themoonha.data.model.lounge

/**
 * 라운지 홈 응답 DTO
 * @author 조희정
 * @since 2024.08.30
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.30  	조희정       최초 생성
 * </pre>
 */
data class LoungeHomeResponse(
    val loungeInfo: LoungeInfo,
    val loungeNoticePostList: List<LoungeNoticePost>,
    val loungePostList: List<LoungePost>,
    val attendanceList: AttendanceMembersResponse,
    val loungeMemberList: List<LoungeMember>
) {
    data class LoungeInfo(
        val lessonId: Long,
        val title: String,
        val loungeImgUrl: String,
        val tutorId: Int,
        val tutorName: String,
        val tutorImgUrl: String,
        val summary: String,
        val permissionYn: Boolean
    )

    data class LoungeNoticePost(
        val loungePostId: Long,
        val content: String,
        val noticeYn: Boolean,
        val loungePostImgList: List<String>,
        val createdAt: String,
        val loungeMember: LoungeMember
    )

    data class LoungePost(
        val loungePostId: Long,
        val content: String,
        val noticeYn: Boolean,
        val loungePostImgList: List<String>,
        val createdAt: String,
        val loungeMember: LoungeMember,
        val permissionYn: Boolean
    )

    data class LoungeMember(
        val memberId: Long,
        val name: String,
        val profileImgUrl: String
    )
}

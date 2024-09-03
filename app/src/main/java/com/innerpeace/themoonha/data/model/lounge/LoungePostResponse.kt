package com.innerpeace.themoonha.data.model.lounge

/**
 * 라운지 게시글 상세 조회 Response
 * @author 조희정
 * @since 2024.09.01
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.01  	조희정       최초 생성
 * </pre>
 */
data class LoungePostResponse(
    val loungePost: LoungePost,
    val loungeCommentList: List<LoungeComment>
) {
    data class LoungePost(
        val loungePostId: Long,
        val content: String,
        val noticeYn: Boolean,
        val loungePostImgList: List<String>,
        val createdAt: String,
        val loungeMember: LoungeMember
    )

    data class LoungeComment(
        val loungeCommentId: Long,
        val content: String,
        val createdAt: String,
        val loungeMember: LoungeMember
    )

    data class LoungeMember(
        val memberId: Long,
        val name: String,
        val profileImgUrl: String
    )
}
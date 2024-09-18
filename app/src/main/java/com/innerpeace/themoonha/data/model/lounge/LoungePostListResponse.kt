package com.innerpeace.themoonha.data.model.lounge

/**
 * 라운지 게시물 목록 응답
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
data class LoungePostListResponse(
    val loungePostId: Long,
    val content: String,
    val noticeYn: Boolean,
    val loungePostImgList: List<String>,
    val createdAt: String,
    val loungeMember: LoungeHomeResponse.LoungeMember,
    val permissionYn: Boolean
)

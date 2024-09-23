package com.innerpeace.themoonha.data.model.lounge

/**
 * 라운지 목록 응답 DTO
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
data class LoungeListResponse(
    val loungeId: Long,
    val title: String,
    val loungeImgUrl: String,
    val latestPostTime: String
)
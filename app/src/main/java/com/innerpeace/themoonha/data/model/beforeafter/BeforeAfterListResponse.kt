package com.innerpeace.themoonha.data.model.beforeafter

/**
 * BeforeAfterDetailResponse 데이터 클래스
 * @author 김진규
 * @since 2024.09.04
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.04  	김진규       최초 생성
 * </pre>
 */
data class BeforeAfterListResponse(
    val beforeAfterId: Long,
    val beforeThumbnailUrl: String,
    val afterThumbnailUrl: String,
    val title: String,
    val profileImgUrl: String,
    val memberName: String
)

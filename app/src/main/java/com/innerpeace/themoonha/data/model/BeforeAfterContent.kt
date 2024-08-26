package com.innerpeace.themoonha.data.model

/**
 * Before&After 콘텐츠 데이터 클래스
 * @author 김진규
 * @since 2024.08.25
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.25  	김진규       최초 생성
 * </pre>
 */
data class BeforeAfterContent (
    val beforeUrl: String,
    val beforeIsImage: Boolean,
    val afterUrl: String,
    val afterIsImage: Boolean,
    val title: String,
    val profileImageUrl: String,
    val memberName: String
)
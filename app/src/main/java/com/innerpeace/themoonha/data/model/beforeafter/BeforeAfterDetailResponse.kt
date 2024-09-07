package com.innerpeace.themoonha.data.model.beforeafter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
@Parcelize
data class BeforeAfterDetailResponse(
    val beforeUrl: String,
    val beforeIsImage: Int,
    val afterUrl: String,
    val afterIsImage: Int,
    val title: String,
    val profileImgUrl: String,
    val memberName: String,
    val hashtags: List<String>
) : Parcelable

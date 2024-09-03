package com.innerpeace.themoonha.data.model.beforeafter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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

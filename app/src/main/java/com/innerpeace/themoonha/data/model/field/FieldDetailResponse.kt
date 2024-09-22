package com.innerpeace.themoonha.data.model.field

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FieldDetailResponse(
    val lessonId: Long,
    val contentUrl: String,
    val contentIsImage: Int,
    val title: String,
    val profileImgUrl: String,
    val memberName: String,
    val hashtags: List<String>
) : Parcelable

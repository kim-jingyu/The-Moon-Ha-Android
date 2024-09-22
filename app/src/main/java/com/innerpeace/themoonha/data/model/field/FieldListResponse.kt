package com.innerpeace.themoonha.data.model.field

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FieldListResponse(
    val lessonTitle: String,
    val categoryId: Long,
    val category: String,
    val fieldId: Long,
    val thumbnailUrl:String,
    val title: String,
    val profileImgUrl: String,
    val memberName: String
) : Parcelable

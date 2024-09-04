package com.innerpeace.themoonha.data.model.field

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FieldListResponse(
    val categoryId: Long,
    val categoryName: String,
    val fieldId: Long,
    val thumbnailUrl:String,
    val title: String,
    val profileImgUrl: String,
    val memberName: String
) : Parcelable

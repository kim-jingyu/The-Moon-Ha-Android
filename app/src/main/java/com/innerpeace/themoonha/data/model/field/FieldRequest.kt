package com.innerpeace.themoonha.data.model.field

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FieldRequest(
    val lessonId: Long,
    val title: String,
    val hashtags: List<String>
) : Parcelable

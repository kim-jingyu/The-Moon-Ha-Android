package com.innerpeace.themoonha.data.model.live

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LiveLessonRequest(
    val memberId: Long,
    val lessonId: Long,
    val title: String,
    val description: String,
    val status: LiveStatus,
    val streamKey: String,
    val thumbnailUrl: String
) : Parcelable


package com.innerpeace.themoonha.data.model.live

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LiveLessonResponse(
    val liveId: Long,
    val lessonId: Long,
    val title: String,
    val streamKey: String,
    val description: String,
    val profileImgUrl: String,
    val instructorName: String,
    val thumbnailUrl: String,
    val broadcastUrl: String,
    val status: LiveStatus,
    val minutesAgo: Long
) : Parcelable


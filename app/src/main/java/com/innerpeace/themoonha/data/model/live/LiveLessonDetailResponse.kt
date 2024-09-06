package com.innerpeace.themoonha.data.model.live

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class LiveLessonDetailResponse(
    val liveId: Long,
    val title: String,
    val description: String,
    val streamKey: String,
    val profileImgUrl: String,
    val instructorName: String,
    val thumbnailUrl: String,
    val broadcastUrl: String,
    val status: LiveStatus,
    val createdAt: Date,
    val minutesAgo: Long,
    val isEnrolled: Boolean
) : Parcelable


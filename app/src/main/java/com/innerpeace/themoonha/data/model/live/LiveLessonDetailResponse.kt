package com.innerpeace.themoonha.data.model.live

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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
    val createdAt: Long,
    val minutesAgo: Long,
    val isEnrolled: Boolean,
    val summary: String,
    val curriculum: String,
    val supply: String
) : Parcelable


package com.innerpeace.themoonha.data.model.live

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class LiveLesson(
    val liveId: Long,
    val memberId: Long,
    val lessonId: Long,
    val title: String,
    val description: String,
    val status: LiveStatus,
    val streamKey: String,
    val broadcastUrl: String,
    val thumbnailUrl: String,
    val createdAt: Date,
    val deletedAt: Date?,
    val updatedAt: Date?
) : Parcelable
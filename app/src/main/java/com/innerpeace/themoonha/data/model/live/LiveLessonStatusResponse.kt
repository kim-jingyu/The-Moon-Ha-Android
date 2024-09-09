package com.innerpeace.themoonha.data.model.live

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LiveLessonStatusResponse(
    val liveId: Long,
    val status: LiveStatus
) : Parcelable {
    companion object {
        fun from(liveLesson: LiveLesson): LiveLessonStatusResponse {
            return LiveLessonStatusResponse(
                liveId = liveLesson.liveId,
                status = liveLesson.status
            )
        }
    }
}

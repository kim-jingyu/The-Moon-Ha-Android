package com.innerpeace.themoonha.data.model.craft

data class WishLessonDTO(
    val wishLessonId: Long,
    val title: String,
    var voteCnt: Int,
    val theme: String,
    var alreadyVoted: Boolean
)

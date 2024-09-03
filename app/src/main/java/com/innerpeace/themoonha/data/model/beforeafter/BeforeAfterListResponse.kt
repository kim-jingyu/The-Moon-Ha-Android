package com.innerpeace.themoonha.data.model.beforeafter

data class BeforeAfterListResponse(
    val beforeAfterId: Long,
    val beforeThumbnailUrl: String,
    val afterThumbnailUrl: String,
    val title: String,
    val profileImgUrl: String,
    val memberName: String
)

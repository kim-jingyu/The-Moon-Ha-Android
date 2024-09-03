package com.innerpeace.themoonha.data.model.beforeafter

data class BeforeAfterListResponse(
    private val beforeThumbnailUrl: String,
    private val afterThumbnailUrl: String,
    private val title: String,
    private val profileImgUrl: String,
    private val memberName: String
)

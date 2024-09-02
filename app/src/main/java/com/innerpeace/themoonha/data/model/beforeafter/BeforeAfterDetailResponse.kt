package com.innerpeace.themoonha.data.model.beforeafter

data class BeforeAfterDetailResponse(
    private val beforeUrl: String,
    private val beforeIsImage: Int,
    private val afterUrl: String,
    private val afterIsImage: Int,
    private val title: String,
    private val profileImgUrl: String,
    private val memberName: String,
    private val hashtags: List<String>
)

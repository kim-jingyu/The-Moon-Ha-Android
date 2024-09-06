package com.innerpeace.themoonha.data.model.lesson

data class CartResponse(
    val branchName: String,
    val cartId: String,
    val lessonTitle: String,
    val period: String,
    val lessonTime: String,
    val tutorName: String,
    val target: String,
    val cost: Int,
    val onlineCost: Int,
    val onlineYn: Boolean
)

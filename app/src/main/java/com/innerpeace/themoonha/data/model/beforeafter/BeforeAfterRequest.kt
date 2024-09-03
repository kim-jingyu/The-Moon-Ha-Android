package com.innerpeace.themoonha.data.model.beforeafter

data class BeforeAfterRequest(
    val lessonId: Long,
    val title: String,
    val hashtags: List<String>
)

package com.innerpeace.themoonha.data.model.beforeafter

data class BeforeAfterRequest(
    private val lessonId: Long,
    private val title: String,
    private val hashtags: List<String>
)

package com.innerpeace.themoonha.data.model.craft

data class PrologueDTO(
    val prologueId: Long,
    val prologueThemeId: Long,
    val tutorName: String?,
    val themeName: String,
    val themeDescription: String,
    val title: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val likeCnt: Int,
    val alreadyLiked: Boolean
)

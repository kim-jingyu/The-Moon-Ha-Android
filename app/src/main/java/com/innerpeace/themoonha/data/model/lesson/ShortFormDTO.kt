package com.innerpeace.themoonha.data.model.lesson

data class ShortFormDTO(
    val shortFormId: Long,
    val name: String,
    val thumbnailUrl: String,
    val target: Int

) {
    fun getTargetDescription(): String {
        return TargetType.fromId(target)?.description ?: "전체"
    }
}



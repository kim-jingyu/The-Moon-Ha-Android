package com.innerpeace.themoonha.data.model.lesson

enum class TargetType(val description: String) {
    ADULT("성인"),
    PARENT_AND_CHILD("엄마랑 아가랑"),
    CHILD("유아/어린이"),
    FAMILY("패밀리");

    companion object {
        fun fromId(id: Int): TargetType? {
            return values().find { it.ordinal + 1 == id }
        }
    }
}
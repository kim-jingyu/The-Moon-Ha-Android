package com.innerpeace.themoonha.data.model.craft

data class CraftMainResponse(
    var prologueList: List<PrologueDTO>,
    val firstWishLessonList: List<WishLessonDTO>,
    val secondWishLessonList: List<WishLessonDTO>,
    val suggestionList: List<SuggestionDTO>,
    val pageDTO: PageDTO
)

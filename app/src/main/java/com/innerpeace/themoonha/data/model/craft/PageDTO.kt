package com.innerpeace.themoonha.data.model.craft


data class PageDTO(
    val pageCount: Int,
    val startPage: Int,
    val endPage: Int,
    val realEnd: Int,
    val prev: Boolean,
    val next: Boolean,
    val total: Int,
    val criteria: Criteria
)

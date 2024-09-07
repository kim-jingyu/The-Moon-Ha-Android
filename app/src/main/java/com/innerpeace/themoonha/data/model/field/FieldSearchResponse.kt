package com.innerpeace.themoonha.data.model.field

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FieldSearchResponse(
    val fieldId: Long,
    val title: String
) : Parcelable

package com.innerpeace.themoonha.viewModel

import androidx.lifecycle.ViewModel
import okhttp3.MultipartBody

class BeforeAfterEnrollContentsViewModel : ViewModel() {
    var beforeContentPart: MultipartBody.Part? = null
    var afterContentPart: MultipartBody.Part? = null
}
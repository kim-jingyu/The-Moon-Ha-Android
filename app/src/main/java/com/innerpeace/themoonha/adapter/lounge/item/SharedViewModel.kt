package com.innerpeace.themoonha.adapter.lounge.item

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val isScrollEnabled = MutableLiveData<Boolean>()

    fun setScrollEnabled(enabled: Boolean) {
        isScrollEnabled.value = enabled
    }
}
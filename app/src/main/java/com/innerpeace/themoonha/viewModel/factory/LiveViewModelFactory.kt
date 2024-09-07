package com.innerpeace.themoonha.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.innerpeace.themoonha.data.repository.LiveRepository
import com.innerpeace.themoonha.viewmodel.LiveViewModel

class LiveViewModelFactory(private val datasource: LiveRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiveViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LiveViewModel(datasource) as T
        }
        throw IllegalArgumentException("뷰 모델이 불일치합니다.")
    }
}
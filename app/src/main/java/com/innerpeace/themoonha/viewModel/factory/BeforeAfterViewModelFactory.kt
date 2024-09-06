package com.innerpeace.themoonha.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.innerpeace.themoonha.data.repository.BeforeAfterRepository
import com.innerpeace.themoonha.viewmodel.BeforeAfterViewModel

class BeforeAfterViewModelFactory(private val dataSource: BeforeAfterRepository) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeforeAfterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BeforeAfterViewModel(dataSource) as T
        }
        throw IllegalArgumentException("뷰 모델이 불일치합니다.")
    }
}
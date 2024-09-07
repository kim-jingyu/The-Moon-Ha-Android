package com.innerpeace.themoonha.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.innerpeace.themoonha.data.repository.FieldRepository
import com.innerpeace.themoonha.viewmodel.FieldViewModel

class FieldViewModelFactory(private val dataSource: FieldRepository) :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FieldViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FieldViewModel(dataSource) as T
        }
        throw IllegalArgumentException("뷰 모델이 불일치합니다.")
    }
}
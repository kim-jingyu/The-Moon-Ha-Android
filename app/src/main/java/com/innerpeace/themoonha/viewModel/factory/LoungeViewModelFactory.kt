package com.innerpeace.themoonha.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.innerpeace.themoonha.data.repository.LoungeRepository
import com.innerpeace.themoonha.viewModel.LoungeViewModel

class LoungeViewModelFactory(private val repository: LoungeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoungeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoungeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.innerpeace.themoonha.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.innerpeace.themoonha.data.repository.LoungeRepository
import com.innerpeace.themoonha.viewmodel.LoungeViewModel

class LoungeViewModelFactory(private val repository: LoungeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoungeViewModel::class.java)) {
            return LoungeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
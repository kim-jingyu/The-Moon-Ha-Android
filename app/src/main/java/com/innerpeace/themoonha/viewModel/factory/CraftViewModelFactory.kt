package com.innerpeace.themoonha.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.innerpeace.themoonha.data.repository.CraftRepository
import com.innerpeace.themoonha.viewModel.CraftViewModel

class CraftViewModelFactory(private val repository: CraftRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CraftViewModel::class.java)) {
            return CraftViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.innerpeace.themoonha.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.model.craft.CraftMainResponse
import com.innerpeace.themoonha.data.repository.CraftRepository
import kotlinx.coroutines.launch

class CraftViewModel(private val craftRepository: CraftRepository) : ViewModel() {

    private val _craftMainResponse = MutableLiveData<CraftMainResponse?>()
    val craftMainResponse: LiveData<CraftMainResponse?> get() = _craftMainResponse

    fun getCraftMain() {
        viewModelScope.launch {
            val response = craftRepository.fetchCraftMain()
            _craftMainResponse.postValue(response)
        }
    }
}
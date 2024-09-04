package com.innerpeace.themoonha.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.model.craft.CraftMainResponse
import com.innerpeace.themoonha.data.model.craft.PrologueDTO
import com.innerpeace.themoonha.data.model.craft.SuggestionRequest
import com.innerpeace.themoonha.data.model.craft.SuggestionResponse
import com.innerpeace.themoonha.data.repository.CraftRepository
import kotlinx.coroutines.launch

class CraftViewModel(private val craftRepository: CraftRepository) : ViewModel() {

    private val _craftMainResponse = MutableLiveData<CraftMainResponse?>()
    val craftMainResponse: LiveData<CraftMainResponse?> get() = _craftMainResponse

    private val _currentPrologueDetail = MutableLiveData<PrologueDTO?>()
    val currentPrologueDetail: LiveData<PrologueDTO?> get() = _currentPrologueDetail

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> get() = _toastMessage

    private val _suggestionResponse = MutableLiveData<SuggestionResponse?>()
    val suggestionResponse: LiveData<SuggestionResponse?> get() = _suggestionResponse

    fun getCraftMain() {
        viewModelScope.launch {
            val response = craftRepository.fetchCraftMain()
            _craftMainResponse.postValue(response)
        }
    }

    fun setCurrentPrologueDetail(prologue: PrologueDTO) {
        _currentPrologueDetail.postValue(prologue)
    }

    fun likePrologue(prologueId: Long) {
        viewModelScope.launch {
            try {
                val response = craftRepository.fetchPrologueLike(prologueId)
                if (response != null) {
                    if (response.success) {
                        _toastMessage.postValue("좋아요가 성공적으로 반영되었습니다.")
                    } else {
                        _toastMessage.postValue("좋아요 요청에 실패했습니다: ${response.message}")
                    }
                }
            } catch (e: Exception) {
                _toastMessage.postValue("좋아요 요청 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun voteWishLesson(wishLessonId: Long) {
        viewModelScope.launch {
            try {
                val response = craftRepository.fetchWishLessonVote(wishLessonId)
                if (response != null) {
                    if (response.success) {
                        _toastMessage.postValue("투표가 성공적으로 반영되었습니다.")
                    } else {
                        _toastMessage.postValue("투표 요청에 실패했습니다: ${response.message}")
                    }
                }
            } catch (e: Exception) {
                _toastMessage.postValue("투표 요청 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    fun getSuggestionList(pageNum: Int) {
        viewModelScope.launch {
            try {
                val response = craftRepository.fetchSuggestionList(pageNum)
                _suggestionResponse.postValue(response)
            } catch (e: Exception) {
                Log.e("CraftViewModel", "Error fetching suggestion list: ${e.message}")
            }
        }
    }

    fun writeSuggestion(suggestionRequest: SuggestionRequest) {
        viewModelScope.launch {
            val response = craftRepository.fetchSuggestionWrite(suggestionRequest)
            if (response != null && response.success) {

            }
        }
    }
}
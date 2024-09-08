package com.innerpeace.themoonha.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.model.lesson.Branch
import com.innerpeace.themoonha.data.model.lesson.CartRequest
import com.innerpeace.themoonha.data.model.lesson.CartResponse
import com.innerpeace.themoonha.data.model.lesson.LessonDTO
import com.innerpeace.themoonha.data.model.lesson.LessonDetailResponse
import com.innerpeace.themoonha.data.model.lesson.LessonEnrollResponse
import com.innerpeace.themoonha.data.model.lesson.ShortFormDTO
import com.innerpeace.themoonha.data.model.lesson.SugangRequest
import com.innerpeace.themoonha.data.repository.LessonRepository
import kotlinx.coroutines.launch

class LessonViewModel(private val lessonRepository: LessonRepository) : ViewModel() {
    private val _lessonList = MutableLiveData<List<LessonDTO>>()
    val lessonList: LiveData<List<LessonDTO>> get() = _lessonList

    private val _shortFormList = MutableLiveData<List<ShortFormDTO>>()
    val shortFormList: LiveData<List<ShortFormDTO>> get() = _shortFormList

    private val _memberName = MutableLiveData<String>()
    val memberName: LiveData<String> get() = _memberName

    private val _branchName = MutableLiveData<String>()
    val branchName: LiveData<String> get() = _branchName

    private val _lessonDetail = MutableLiveData<LessonDetailResponse>()
    val lessonDetail: LiveData<LessonDetailResponse> get() = _lessonDetail

    private val _lessonCart = MutableLiveData<List<CartResponse>>()
    val lessonCart: LiveData<List<CartResponse>> get() = _lessonCart

    private val _paymentStatus = MutableLiveData<Boolean>()
    val paymentStatus: LiveData<Boolean> get() = _paymentStatus

    private val _lessonEnroll = MutableLiveData<List<LessonEnrollResponse>>()
    val lessonEnroll: LiveData<List<LessonEnrollResponse>> get() = _lessonEnroll

    fun getLessonList(lessonListQueryMap: Map<String, String>) {
        viewModelScope.launch {
            val response = lessonRepository.fetchLessonList(lessonListQueryMap)
            response?.let {
                _lessonList.value = it.lessonList
                _shortFormList.value = it.shortFormList
                _memberName.value = it.memberName
                val branch = Branch.getBranchById(it.branchId)
                _branchName.value = branch?.branchName ?: "모든 지점"
            }
        }
    }

    fun updateBranchName(newBranchName: String) {
        _branchName.value = newBranchName
    }

    fun getLessonDetail(lessonId: Long) {
        viewModelScope.launch {
            val response = lessonRepository.fetchLessonDetail(lessonId)
            response?.let {
                _lessonDetail.value = it
            }
        }
    }

    fun getLessonCart() {
        viewModelScope.launch {
            val response = lessonRepository.fetchLessonCart()
            response?.let {
                _lessonCart.value = it
            }
        }
    }

    fun addLessonCart(cartRequest: CartRequest): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()

        viewModelScope.launch {
            try {
                val response = lessonRepository.fetchAddLessonCart(cartRequest)
                if (response != null) {
                    result.postValue(response.success)
                }
            } catch (e: Exception) {
                result.postValue(false) // 오류 발생 시 실패 처리
            }
        }

        return result
    }

    fun payLesson(sugangRequest: SugangRequest) {
        viewModelScope.launch {
            try {
                val response = lessonRepository.fetchPayLesson(sugangRequest)
                _paymentStatus.postValue(response != null && response.success)
            } catch (e: Exception) {
                _paymentStatus.postValue(false)
            }
        }
    }

    fun getLessonEnroll() {
        viewModelScope.launch {
            val response = lessonRepository.fetchLessonEnroll()
            response?.let {
                _lessonEnroll.value = it
            }
        }
    }
}
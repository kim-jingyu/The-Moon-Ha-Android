package com.innerpeace.themoonha.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.model.lesson.*
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

    fun getLessonEnroll() {
        viewModelScope.launch {
            val response = lessonRepository.fetchLessonEnroll()
            response?.let {
                _lessonEnroll.value = it
            }
        }
    }
}
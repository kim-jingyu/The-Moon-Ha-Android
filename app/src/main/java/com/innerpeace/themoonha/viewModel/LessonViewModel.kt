package com.innerpeace.themoonha.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.model.lesson.*
import com.innerpeace.themoonha.data.repository.LessonRepository
import kotlinx.coroutines.launch

/**
 * 강좌 뷰모델
 *
 * @author 손승완
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	손승완       최초 생성
 * </pre>
 * @since 2024.08.31
 */
class LessonViewModel(private val lessonRepository: LessonRepository) : ViewModel() {
    private val _lessonList = MutableLiveData<List<LessonDTO>>()
    val lessonList: LiveData<List<LessonDTO>> get() = _lessonList

    private val _shortFormList = MutableLiveData<List<ShortFormDTO>>()
    val shortFormList: LiveData<List<ShortFormDTO>> get() = _shortFormList

    private val _currentShortFormId = MutableLiveData<Long>()
    val currentShortFormId: LiveData<Long> get() = _currentShortFormId

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

    private val _lessonFieldEnroll = MutableLiveData<List<LessonEnrollResponse>>()
    val lessonFieldEnroll: LiveData<List<LessonEnrollResponse>> get() = _lessonFieldEnroll

    var currentPage: Int = 0

    private val loadedShortFormIds = mutableSetOf<Long>()

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

    fun getLessonFieldEnroll() {
        viewModelScope.launch {
            val response = lessonRepository.fetchLessonFieldEnroll()
            response?.let {
                _lessonFieldEnroll.value = it
            }
        }
    }

    fun setCurrentShortFormId(lessonId: Long) {
        _currentShortFormId.value = lessonId
    }


    fun getShortFormDetail(shortFormId: Long) {
        if (loadedShortFormIds.contains(shortFormId)) return

        loadedShortFormIds.add(shortFormId)

        viewModelScope.launch {
            lessonRepository.fetchShortFormDetail(shortFormId)
        }
    }

    fun removeCartItem(cartId: Long) {
        viewModelScope.launch {
            lessonRepository.fetchDeleteCart(cartId)
            val updatedCartList = _lessonCart.value?.filter { it.cartId.toLong() != cartId } ?: emptyList()
            _lessonCart.value = updatedCartList
        }
    }
}
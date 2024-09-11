package com.innerpeace.themoonha.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.exception.BeforeAfterException
import com.innerpeace.themoonha.data.exception.BeforeAfterMakingException
import com.innerpeace.themoonha.data.exception.BeforeAfterRetrievingException
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterDetailResponse
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterListResponse
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterSearchResponse
import com.innerpeace.themoonha.data.repository.BeforeAfterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * 비포애프터 API ViewModel
 * @author 김진규
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  	김진규       최초 생성
 * </pre>
 */
class BeforeAfterViewModel(private val datasource: BeforeAfterRepository) : ViewModel() {
    private val _beforeAfterListContents = MutableStateFlow<List<BeforeAfterListResponse>>(emptyList())
    private val _beforeAfterDetailContents = MutableStateFlow<List<BeforeAfterDetailResponse>>(emptyList())
    private val _beforeAfterSearchContents = MutableStateFlow<List<BeforeAfterSearchResponse>>(emptyList())
    private val _makeBeforeAfterResponse = MutableStateFlow(Result.success(""))
    private val _error = MutableStateFlow<BeforeAfterException?>(null)

    val beforeAfterListResponse: StateFlow<List<BeforeAfterListResponse>> get() = _beforeAfterListContents.asStateFlow()
    val beforeAfterDetailResponse: StateFlow<List<BeforeAfterDetailResponse>> get() = _beforeAfterDetailContents.asStateFlow()
    val beforeAfterSearchResponse: StateFlow<List<BeforeAfterSearchResponse>> get() = _beforeAfterSearchContents.asStateFlow()
    val makeBeforeAfterResponse: StateFlow<Result<String>> = _makeBeforeAfterResponse.asStateFlow()
    val error: StateFlow<BeforeAfterException?> get() = _error.asStateFlow()

    fun getBeforeAfterList() {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveBeforeAfterList()
                if (response.isSuccessful && response.body() != null) {
                    _beforeAfterListContents.value = response.body()!!
                } else {
                    _error.value = BeforeAfterRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = BeforeAfterRetrievingException()
            }
        }
    }

    fun getBeforeAfterListOrderByTitle() {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveBeforeAfterListOrderByTitle()
                if (response.isSuccessful && response.body() != null) {
                    _beforeAfterListContents.value = response.body()!!
                } else {
                    _error.value = BeforeAfterRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = BeforeAfterRetrievingException()
            }
        }
    }

    fun getBeforeAfterDetails(selectedPosition: Int) {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveBeforeAfterContents()
                if (response.isSuccessful && response.body() != null) {
                    val beforeAfterDetails = response.body()!!
                    val selectedItem = beforeAfterDetails[selectedPosition]
                    println("selectedItem = ${selectedItem}")

                    val sortedDetails = mutableListOf(selectedItem).apply {
                        addAll(beforeAfterDetails.filterIndexed { index, _ -> index != selectedPosition })
                    }

                    _beforeAfterDetailContents.value = sortedDetails
                } else {
                    _error.value = BeforeAfterRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = BeforeAfterRetrievingException()
            }
        }
    }

    fun makeBeforeAfter(
        beforeAfterRequest: RequestBody,
        beforeThumbnail: MultipartBody.Part,
        afterThumbnail: MultipartBody.Part,
        beforeContent: MultipartBody.Part,
        afterContent: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                val response = datasource.makeBeforeAfter(
                    beforeAfterRequest,
                    beforeThumbnail,
                    afterThumbnail,
                    beforeContent,
                    afterContent
                )
                if (response.success) {
                    _makeBeforeAfterResponse.value = Result.success(response.message)
                } else {
                    _makeBeforeAfterResponse.value = Result.failure(BeforeAfterMakingException())
                }
            } catch (e: Exception) {
                Log.e("API Error", "Error making BeforeAfter", e)
                _makeBeforeAfterResponse.value = Result.failure(BeforeAfterMakingException())
            }
        }

    }

    fun searchBeforeAfterByTitle(keyword: String) {
        viewModelScope.launch {
            try {
                val response = datasource.searchBeforeAfterByTitle(keyword)
                if (response.isSuccessful && response.body() != null) {
                    _beforeAfterSearchContents.value = response.body()!!
                } else {
                    _error.value = BeforeAfterRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = BeforeAfterRetrievingException()
            }
        }
    }

    fun searchBeforeAfterByHashtag(hashtags: List<String>) {
        viewModelScope.launch {
            try {
                val response = datasource.searchBeforeAfterByHashtag(hashtags)
                if (response.isSuccessful && response.body() != null) {
                    _beforeAfterSearchContents.value = response.body()!!
                } else {
                    _error.value = BeforeAfterRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = BeforeAfterRetrievingException()
            }
        }
    }
}
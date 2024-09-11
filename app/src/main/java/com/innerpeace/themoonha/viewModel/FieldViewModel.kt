package com.innerpeace.themoonha.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.exception.FieldException
import com.innerpeace.themoonha.data.exception.FieldMakingException
import com.innerpeace.themoonha.data.exception.FieldRetrievingException
import com.innerpeace.themoonha.data.model.field.FieldDetailResponse
import com.innerpeace.themoonha.data.model.field.FieldListResponse
import com.innerpeace.themoonha.data.model.field.FieldSearchResponse
import com.innerpeace.themoonha.data.repository.FieldRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * 분야별 한 입 API ViewModel
 * @author 김진규
 * @since 2024.09.04
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.04  	김진규       최초 생성
 * </pre>
 */
class FieldViewModel(private val datasource: FieldRepository) : ViewModel() {
    private val _fieldListContents = MutableStateFlow<List<FieldListResponse>>(emptyList())
    private val _fieldDetailContents = MutableStateFlow<List<FieldDetailResponse>>(emptyList())
    private val _fieldSearchContents = MutableStateFlow<List<FieldSearchResponse>>(emptyList())
    private val _makeFieldResponse = MutableStateFlow(Result.success(""))
    private val _error = MutableStateFlow<FieldException?>(null)

    val fieldListResponse: StateFlow<List<FieldListResponse>> get() = _fieldListContents.asStateFlow()
    val fieldDetailResponses: StateFlow<List<FieldDetailResponse>> get() = _fieldDetailContents.asStateFlow()
    val fieldSearchResponse: StateFlow<List<FieldSearchResponse>> get() = _fieldSearchContents.asStateFlow()
    val makeFieldResponse: StateFlow<Result<String>> = _makeFieldResponse.asStateFlow()
    val error: StateFlow<FieldException?> get() = _error.asStateFlow()

    fun getFieldList() {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveFieldList()
                if (response.isSuccessful && response.body() != null) {
                    _fieldListContents.value = response.body()!!
                } else {
                    _error.value = FieldRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = FieldRetrievingException()
            }
        }
    }

    fun getFieldListOrderByTitle() {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveFieldListOrderByTitle()
                if (response.isSuccessful && response.body() != null) {
                    _fieldListContents.value = response.body()!!
                } else {
                    _error.value = FieldRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = FieldRetrievingException()
            }
        }
    }

    fun getFieldDetails(selectedPosition: Int) {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveFieldContents()
                if (response.isSuccessful && response.body() != null) {
                    val fieldDetails = response.body()!!
                    val selectedItem = fieldDetails[selectedPosition]

                    val sortedDetails = mutableListOf(selectedItem).apply {
                        addAll(fieldDetails.filterIndexed { index, _ -> index != selectedPosition })
                    }

                    _fieldDetailContents.value = sortedDetails
                } else {
                    _error.value = FieldRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = FieldRetrievingException()
            }
        }
    }

    fun makeField(
        fieldRequest: RequestBody,
        thumbnail: MultipartBody.Part,
        content: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                val response = datasource.makeField(
                    fieldRequest,
                    thumbnail,
                    content
                )
                if (response.success) {
                    _makeFieldResponse.value = Result.success(response.message)
                } else {
                    _makeFieldResponse.value = Result.failure(FieldMakingException())
                }
            } catch (e: Exception) {
                _makeFieldResponse.value = Result.failure(FieldMakingException())
            }
        }
    }

    fun searchFieldByTitle(keyword: String) {
        viewModelScope.launch {
            try {
                val response = datasource.searchFieldByTitle(keyword)
                if (response.isSuccessful && response.body() != null) {
                    _fieldSearchContents.value = response.body()!!
                } else {
                    _error.value = FieldRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = FieldRetrievingException()
            }
        }
    }

    fun searchFieldByHashtag(hashtags: List<String>) {
        viewModelScope.launch {
            try {
                val response = datasource.searchFieldByHashtag(hashtags)
                if (response.isSuccessful && response.body() != null) {
                    _fieldSearchContents.value = response.body()!!
                } else {
                    _error.value = FieldRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = FieldRetrievingException()
            }
        }
    }
}
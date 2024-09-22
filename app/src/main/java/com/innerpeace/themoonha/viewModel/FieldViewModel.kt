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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val _fieldDetailContent = MutableStateFlow<FieldDetailResponse?>(null)
    private val _fieldSearchContents = MutableStateFlow<List<FieldSearchResponse>>(emptyList())
    private val _makeFieldResponse = MutableStateFlow(Result.success(""))
    private val _error = MutableStateFlow<FieldException?>(null)

    val fieldListResponse: StateFlow<List<FieldListResponse>> get() = _fieldListContents.asStateFlow()
    val fieldDetailContent: StateFlow<FieldDetailResponse?> get() = _fieldDetailContent.asStateFlow()
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

    fun getFieldListByCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveFieldListByCategory(categoryId)
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

    fun getFieldDetail(fieldId: Long) {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveFieldContent(fieldId)
                if (response.isSuccessful && response.body() != null) {
                    _fieldDetailContent.value = response.body()!!
                } else {
                    _fieldDetailContent.value = null
                    _error.value = FieldRetrievingException()
                }
            } catch (e: Exception) {
                _fieldDetailContent.value = null
                _error.value = FieldRetrievingException()
            }
        }
    }

    suspend fun makeField(
        fieldRequest: RequestBody,
        thumbnail: MultipartBody.Part,
        content: MultipartBody.Part
    ) : Result<String> {
        return try {
            Result.success(withContext(Dispatchers.IO) {
                datasource.makeField(fieldRequest, thumbnail, content)
            }.message)
        } catch (e: Exception) {
            Result.failure(FieldMakingException())
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

    fun clearFieldDetail() {
        _fieldDetailContent.value = null
    }
}
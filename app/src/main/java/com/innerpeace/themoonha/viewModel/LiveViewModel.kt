package com.innerpeace.themoonha.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.exception.LiveException
import com.innerpeace.themoonha.data.exception.LiveJoinException
import com.innerpeace.themoonha.data.exception.LiveLeaveException
import com.innerpeace.themoonha.data.exception.LiveLikeException
import com.innerpeace.themoonha.data.exception.LiveRetrievingException
import com.innerpeace.themoonha.data.exception.LiveSharedException
import com.innerpeace.themoonha.data.model.live.LiveLessonDetailResponse
import com.innerpeace.themoonha.data.model.live.LiveLessonResponse
import com.innerpeace.themoonha.data.repository.LiveRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 실시간 강좌 API ViewModel
 * @author 김진규
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	김진규       최초 생성
 * </pre>
 */
class LiveViewModel(private val datasource: LiveRepository) : ViewModel() {
    private val _liveLessonList = MutableStateFlow<List<LiveLessonResponse>>(emptyList())
    private val _liveLessonDetail = MutableStateFlow<LiveLessonDetailResponse?>(null)
    private val _liveLessonViewersCount = MutableStateFlow<Int?>(null)
    private val _liveLessonLikesCount = MutableStateFlow<Int?>(null)
    private val _liveLessonShareLink = MutableStateFlow<String>("")
    private val _error = MutableStateFlow<LiveException?>(null)

    val liveLessonListResponse: StateFlow<List<LiveLessonResponse>> get() = _liveLessonList.asStateFlow()
    val liveLessonDetailResponse: StateFlow<LiveLessonDetailResponse?> get() = _liveLessonDetail.asStateFlow()
    val liveLessonViewersCountResponse: StateFlow<Int?> get() = _liveLessonViewersCount.asStateFlow()
    val liveLessonLikesCountResponse: StateFlow<Int?> get() = _liveLessonLikesCount.asStateFlow()
    val liveLessonShareLinkResponse: StateFlow<String> get() = _liveLessonShareLink.asStateFlow()
    val error: StateFlow<LiveException?> get() = _error.asStateFlow()

    fun getLiveLessonListWithMember() {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveLiveLessonListWithMember()
                if (response.isSuccessful && response.body() != null) {
                    _liveLessonList.value = response.body()!!
                } else {
                    _error.value = LiveRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = LiveRetrievingException()
            }
        }
    }

    fun getLiveLessonListWithMemberOrderByTitle() {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveLiveLessonListWithMemberOrderByTitle()
                if (response.isSuccessful && response.body() != null) {
                    _liveLessonList.value = response.body()!!
                } else {
                    _error.value = LiveRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = LiveRetrievingException()
            }
        }
    }

    fun getLiveLessonListWithoutMember() {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveLiveLessonListWithoutMember()
                if (response.isSuccessful && response.body() != null) {
                    _liveLessonList.value = response.body()!!
                } else {
                    _error.value = LiveRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = LiveRetrievingException()
            }
        }
    }

    fun getLiveLessonListWithoutMemberOrderByTitle() {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveLiveLessonListWithoutMemberOrderByTitle()
                if (response.isSuccessful && response.body() != null) {
                    _liveLessonList.value = response.body()!!
                } else {
                    _error.value = LiveRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = LiveRetrievingException()
            }
        }
    }

    fun getLiveLessonDetail(liveId: Long) {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveLiveLessonDetail(liveId)
                if (response.isSuccessful && response.body() != null) {
                    _liveLessonDetail.value = response.body()!!
                } else {
                    _error.value = LiveRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = LiveRetrievingException()
            }
        }
    }

    fun getViewersCount(liveId: Long) {
        viewModelScope.launch {
            try {
                val response = datasource.getViewersCount(liveId)
                if (response.isSuccessful) {
                    _liveLessonViewersCount.value = response.body()
                } else {
                    _error.value = LiveRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = LiveRetrievingException()
            }
        }
    }

    fun getLikesCount(liveId: Long) {
        viewModelScope.launch {
            try {
                val response = datasource.getLLikesCount(liveId)
                if (response.isSuccessful) {
                    _liveLessonLikesCount.value = response.body()
                } else {
                    _error.value = LiveRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = LiveRetrievingException()
            }
        }
    }

    suspend fun joinLiveLesson(liveId: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                datasource.joinLiveLesson(liveId)
            }
            Result.success(1)
        } catch (e: Exception) {
            Result.failure(LiveJoinException())
        }
    }

    suspend fun leaveLiveLesson(liveId: Long): Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                datasource.leaveLiveLesson(liveId)
            }
            Result.success(1)
        } catch (e: Exception) {
            Result.failure(LiveLeaveException())
        }
    }

    suspend fun likeLiveLesson(liveId: Long) : Result<Int> {
        return try {
            withContext(Dispatchers.IO) {
                datasource.likeLiveLesson(liveId)
            }
            Result.success(1)
        } catch (e: Exception) {
            Result.failure(LiveLikeException())
        }
    }

    fun getShareLink(liveId: Long) {
        viewModelScope.launch {
            try {
                val response = datasource.getShareLink(liveId)
                if (response.isSuccessful) {
                    _liveLessonShareLink.value = response.body()!!
                } else {
                    _error.value = LiveSharedException()
                }
            } catch (e: Exception) {
                _error.value = LiveSharedException()
            }
        }
    }
}
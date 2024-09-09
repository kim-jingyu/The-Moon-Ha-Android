package com.innerpeace.themoonha.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.exception.*
import com.innerpeace.themoonha.data.model.live.LiveLessonDetailResponse
import com.innerpeace.themoonha.data.model.live.LiveLessonResponse
import com.innerpeace.themoonha.data.model.live.LiveLessonStatusResponse
import com.innerpeace.themoonha.data.repository.LiveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

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
    private val _liveLessonStatus = MutableStateFlow<LiveLessonStatusResponse?>(null)
    private val _liveLessonViewersCount = MutableStateFlow<Int?>(null)
    private val _liveLessonLikesCount = MutableStateFlow<Int?>(null)
    private val _liveLessonJoinCount = MutableStateFlow<Int?>(null)
    private val _liveLessonLeaveCount = MutableStateFlow<Int?>(null)
    private val _liveLessonLikeCount = MutableStateFlow<Int?>(null)
    private val _liveLessonShareLink = MutableStateFlow<String>("")
    private val _makeLiveLesson = MutableStateFlow(Result.success(""))
    private val _endLiveLesson = MutableStateFlow(Result.success(""))
    private val _error = MutableStateFlow<LiveException?>(null)

    val liveLessonListResponse: StateFlow<List<LiveLessonResponse>> get() = _liveLessonList.asStateFlow()
    val liveLessonDetailResponse: StateFlow<LiveLessonDetailResponse?> get() = _liveLessonDetail.asStateFlow()
    val liveLessonStatusResponse: StateFlow<LiveLessonStatusResponse?> get() = _liveLessonStatus.asStateFlow()
    val makeLiveLessonResponse: StateFlow<Result<String>> = _makeLiveLesson.asStateFlow()
    val endLiveLessonResponse: StateFlow<Result<String>> = _endLiveLesson.asStateFlow()
    val liveLessonViewersCountResponse: StateFlow<Int?> get() = _liveLessonViewersCount.asStateFlow()
    val liveLessonLikesCountResponse: StateFlow<Int?> get() = _liveLessonLikesCount.asStateFlow()
    val liveLessonJoinCountResponse: StateFlow<Int?> get() = _liveLessonJoinCount.asStateFlow()
    val liveLessonLeaveCountResponse: StateFlow<Int?> get() = _liveLessonLeaveCount.asStateFlow()
    val liveLessonCountResponse: StateFlow<Int?> get() = _liveLessonLikeCount.asStateFlow()
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

    fun makeLesson(
        liveLessonRequest: RequestBody,
        thumbnail: MultipartBody.Part
    ) {
        viewModelScope.launch {
            try {
                val response = datasource.makeLiveLesson(
                    liveLessonRequest = liveLessonRequest,
                    thumbnail = thumbnail
                )
                if (response.success) {
                    _makeLiveLesson.value = Result.success(response.message)
                } else {
                    _error.value = LiveMakingException()
                }
            } catch (e: Exception) {
                _error.value = LiveMakingException()
            }
        }
    }

    fun endLiveLesson(liveId: Long) {
        viewModelScope.launch {
            try {
                val response = datasource.endLiveLesson(liveId)
                if (response.success) {
                    _endLiveLesson.value = Result.success(response.message)
                } else {
                    _error.value = LiveRetrievingException()
                }
            } catch (e: Exception) {
                _error.value = LiveRetrievingException()
            }
        }
    }

    fun getLiveLessonStatus(liveId: Long) {
        viewModelScope.launch {
            try {
                val response = datasource.retrieveLiveLessonStatus(liveId)
                if (response.isSuccessful) {
                    _liveLessonStatus.value = response.body()!!
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

    fun joinLiveLesson(liveId: Long) {
        viewModelScope.launch {
            try {
                val response = datasource.joinLiveLesson(liveId)
                if (response.isSuccessful) {
                    _liveLessonJoinCount.value = response.body()
                } else {
                    _error.value = LiveJoinException()
                }
            } catch (e: Exception) {
                _error.value = LiveJoinException()
            }
        }
    }

    fun leaveLiveLesson(liveId: Long) {
        viewModelScope.launch {
            try {
                val response = datasource.leaveLiveLesson(liveId)
                if (response.isSuccessful) {
                    _liveLessonLeaveCount.value = response.body()
                } else {
                    _error.value = LiveLeaveException()
                }
            } catch (e: Exception) {
                _error.value = LiveLeaveException()
            }
        }
    }

    fun likeLiveLesson(liveId: Long) {
        viewModelScope.launch {
            try {
                val response = datasource.likeLiveLesson(liveId)
                if (response.isSuccessful) {
                    _liveLessonLikeCount.value = response.body()
                } else {
                    _error.value = LiveLikeException()
                }
            } catch (e: Exception) {
                _error.value = LiveLikeException()
            }
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
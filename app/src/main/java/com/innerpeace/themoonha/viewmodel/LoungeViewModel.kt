package com.innerpeace.themoonha.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.model.CommonResponse
import com.innerpeace.themoonha.data.model.lounge.Attendance
import com.innerpeace.themoonha.data.model.lounge.LoungeCommentRequest
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
import com.innerpeace.themoonha.data.model.lounge.LoungeListResponse
import com.innerpeace.themoonha.data.model.lounge.LoungePostListResponse
import com.innerpeace.themoonha.data.model.lounge.LoungePostRequest
import com.innerpeace.themoonha.data.model.lounge.LoungePostResponse
import com.innerpeace.themoonha.data.repository.LoungeRepository
import kotlinx.coroutines.launch

/**
 * 라운지 ViewModel
 * @author 조희정
 * @since 2024.08.30
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.30  	조희정       최초 생성
 * 2024.08.30   조희정       라운지 목록 불러오기 구현
 * 2024.08.31   조희정       라운지 홈 불러오기 구현
 * 2024.09.02   조희정       라운지 게시글 상세보기 구현
 * 2024.09.03   조희정       라운지 게시글 저장, 댓글 저장 구현
 * 2024.09.09   조희정       출석 시작, 출석 수정 구현
 */

class LoungeViewModel(private val loungeRepository: LoungeRepository) : ViewModel() {

    // 라운지 목록
    private val _loungeList = MutableLiveData<List<LoungeListResponse>?>()
    val loungeList: LiveData<List<LoungeListResponse>?> get() = _loungeList

    // 라운지 홈
    private val _loungeHome = MutableLiveData<LoungeHomeResponse?>()
    val loungeHome: LiveData<LoungeHomeResponse?> get() = _loungeHome

    // 라운지 게시물 목록
    private val _loungePostList = MutableLiveData<List<LoungePostListResponse>?>()
    val loungePostList: LiveData<List<LoungePostListResponse>?> get() = _loungePostList

    // 게시물
    private val _postDetail = MutableLiveData<LoungePostResponse?>()
    val postDetail: LiveData<LoungePostResponse?> get() = _postDetail

    // 출석 시작 리스트
    private val _attendanceStartList = MutableLiveData<List<Attendance>?>()
    val attendanceStartList: LiveData<List<Attendance>?> get() = _attendanceStartList

    // LoungeId
    private val _selectedLoungeId = MutableLiveData<Long>()
    val selectedLoungeId: LiveData<Long> get() = _selectedLoungeId

    // LoungePostId
    private val _selectedLoungePostId = MutableLiveData<Long>()
    val selectedLoungePostId: LiveData<Long> get() = _selectedLoungePostId

    // MemberId
    private val _selectedMemberId = MutableLiveData<Long>()
    val selectedMemberId: LiveData<Long> get() = _selectedMemberId

    // 게시물 저장
    private val _postResponse = MutableLiveData<CommonResponse?>()
    val postResponse: LiveData<CommonResponse?> get() = _postResponse

    // 댓글 저장
    private val _commentResponse = MutableLiveData<CommonResponse?>()
    val commentResponse: LiveData<CommonResponse?> get() = _commentResponse

    // 게시글 페이징
    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    fun setSelectedLoungeId(loungeId: Long) {
        _selectedLoungeId.value = loungeId
    }

    fun setSelectedLoungePostId(postId: Long) {
        _selectedLoungePostId.value = postId
    }

    fun setSelectedMemberId(memberId: Long) {
        _selectedMemberId.value = memberId
    }

    fun fetchLoungeList() {
        viewModelScope.launch {
            val response = loungeRepository.fetchLoungeList()
            _loungeList.postValue(response)
        }
    }

    fun fetchLoungeHome(loungeId: Long) {
        viewModelScope.launch {
            val response = loungeRepository.fetchLoungeHome(loungeId)
            _loungeHome.postValue(response)
        }
    }

    fun hasMoreData(): Boolean {
        return !isLastPage && !isLoading
    }

    fun fetchLoungePostList(loungeId: Long, size: Int) {
        if (isLastPage || isLoading) return

        isLoading = true
        viewModelScope.launch {
            val response = loungeRepository.fetchLoungePostList(loungeId, currentPage, size)

            if (response != null && response.isNotEmpty()) {
                val currentList = _loungePostList.value.orEmpty().toMutableList()
                currentList.addAll(response)
                _loungePostList.postValue(currentList)
                currentPage++
            } else {
                isLastPage = true
            }

            isLoading = false
        }
    }

    fun resetPagination() {
        currentPage = 1
        isLastPage = false
        _loungePostList.value = emptyList()  // 기존 데이터 초기화
    }

    fun fetchPostDetail(loungeId: Long, postId: Long) {
        viewModelScope.launch {
            val response = loungeRepository.fetchPostDetail(loungeId, postId)
            _postDetail.postValue(response)
        }
    }

    fun registerLoungePost(loungePostRequest: LoungePostRequest, imageUris: List<Uri>, context: Context) {
        viewModelScope.launch {
            val response = loungeRepository.registerLoungePost(loungePostRequest, imageUris, context)
            _postResponse.postValue(response)
        }
    }

    fun registerComment(loungeCommentRequest: LoungeCommentRequest) {
        viewModelScope.launch {
            val response = loungeRepository.registerComment(loungeCommentRequest)
            _commentResponse.postValue(response)
        }
    }

    fun startAttendance() {
        val lessonId = _loungeHome.value?.loungeInfo?.lessonId ?: return
        viewModelScope.launch {
            val response = loungeRepository.startAttendance(lessonId)
            _attendanceStartList.postValue(response)
        }
    }

    suspend fun updateAttendanceStatus(attendanceId: Long): Boolean {
        return try {
            val response = loungeRepository.updateAttendanceStatus(attendanceId)
            if (response != null) {
                Log.d("출석", "출석 상태 업데이트 성공")
                true
            } else {
                Log.e("출석", "출석 상태 업데이트 실패")
                false
            }
        } catch (e: Exception) {
            Log.e("출석", "출석 상태 업데이트 실패: ${e.message}")
            false
        }
    }


}

package com.innerpeace.themoonha.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.model.schedule.ScheduleWeeklyResponse
import com.innerpeace.themoonha.data.repository.ScheduleRepository
import kotlinx.coroutines.launch

/**
 * 스케줄 vieModel
 * @author 조희정
 * @since 2024.09.07
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.07  	조희정       최초 생성
 * 2024.09.07  	조희정       주간 스케줄 불러오기 구현
 * </pre>
 */
class ScheduleViewModel(private val scheduleRepository: ScheduleRepository): ViewModel() {

    // 주간 스케줄 목록
    private val _scheduleWeeklyList = MutableLiveData<List<List<ScheduleWeeklyResponse>>?>()
    val scheduleWeeklyList: LiveData<List<List<ScheduleWeeklyResponse>>?> get() = _scheduleWeeklyList

    // 주간 스케줄 기준일
    private val _selectedStandardDate = MutableLiveData<List<String>>()
    val selectedStandardDate: LiveData<List<String>> get() = _selectedStandardDate

    fun setSelectedStandardDates(standardDates: List<String>) {
        _selectedStandardDate.value = standardDates
        Log.d("viewModel-standarddtaes", _selectedStandardDate.value.toString())
    }

    fun fetchScheduleWeeklyList(standardDates: List<String>) {
        viewModelScope.launch {
            try {
                val response = scheduleRepository.fetchScheduleWeeklyList(standardDates)
                _scheduleWeeklyList.postValue(response)
                Log.d("븀호델, schedulelist 변경", response.toString())
            } catch (e: Exception) {
                _scheduleWeeklyList.postValue(null)
            }
        }
    }
}
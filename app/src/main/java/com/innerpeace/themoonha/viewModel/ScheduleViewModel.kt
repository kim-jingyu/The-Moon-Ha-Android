package com.innerpeace.themoonha.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innerpeace.themoonha.data.model.schedule.ScheduleMonthlyResponse
import com.innerpeace.themoonha.data.model.schedule.ScheduleNextResponse
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
 * 2024.09.08  	조희정       월간 스케줄 불러오기 구현
 * 2024.09.08  	조희정       다음 스케줄 불러오기 구현
 * </pre>
 */
class ScheduleViewModel(private val scheduleRepository: ScheduleRepository): ViewModel() {

    // 주간 스케줄 목록
    private val _scheduleWeeklyList = MutableLiveData<List<List<ScheduleWeeklyResponse>>?>()
    val scheduleWeeklyList: LiveData<List<List<ScheduleWeeklyResponse>>?> get() = _scheduleWeeklyList

    // 주간 스케줄 기준일
    private val _selectedStandardDate = MutableLiveData<List<String>>()
    val selectedStandardDate: LiveData<List<String>> get() = _selectedStandardDate

    // 월간 스케줄 목록
    private val _scheduleMonthlyList = MutableLiveData<List<ScheduleMonthlyResponse>?>()
    val scheduleMonthlyList: LiveData<List<ScheduleMonthlyResponse>?> get() = _scheduleMonthlyList

    // 월간 스케줄 기준일
    private val _selectedYearMonth = MutableLiveData<String>()
    val selectedYearMonth: LiveData<String> get() = _selectedYearMonth

    // 다음 스케줄
    private val _scheduleNext = MutableLiveData<ScheduleNextResponse?>()
    val scheduleNext: LiveData<ScheduleNextResponse?> get() = _scheduleNext


    fun setSelectedStandardDate(dates: List<String>) {
        _selectedStandardDate.value = dates
    }

    fun fetchScheduleWeeklyList(standardDates: List<String>) {
        viewModelScope.launch {
            try {
                val response = scheduleRepository.fetchScheduleWeeklyList(standardDates)
                _scheduleWeeklyList.postValue(response)
            } catch (e: Exception) {
                _scheduleWeeklyList.postValue(null)
            }
        }
    }

    fun setSelectedYearMonth(yearMonth: String) {
        _selectedYearMonth.value = yearMonth
    }

    fun fetchScheduleMonthlyList(yearMonth: String) {
        viewModelScope.launch {
            try {
                val response = scheduleRepository.fetchScheduleMonthlyList(yearMonth)
                _scheduleMonthlyList.postValue(response)
            } catch (e: Exception) {
                _scheduleMonthlyList.postValue(null)
            }
        }
    }

    fun fetchScheduleNext() {
        viewModelScope.launch {
            try {
                val response = scheduleRepository.fetchScheduleNext()
                _scheduleNext.postValue(response)
            } catch (e: Exception) {
                _scheduleNext.postValue(null)
            }
        }
    }
}
package com.innerpeace.themoonha.data.repository

import android.util.Log
import com.innerpeace.themoonha.data.model.schedule.ScheduleMonthlyResponse
import com.innerpeace.themoonha.data.model.schedule.ScheduleNextResponse
import com.innerpeace.themoonha.data.model.schedule.ScheduleWeeklyResponse
import com.innerpeace.themoonha.data.network.ScheduleService

/**
 * 스케줄 레포지토리
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
class ScheduleRepository(private val scheduleService: ScheduleService) {

    // 주간 스케줄 목록 불러오기
    suspend fun fetchScheduleWeeklyList(standardDates: List<String>): List<List<ScheduleWeeklyResponse>>? {
        return try {
            scheduleService.getScheduleWeekly(standardDates)
        } catch (e: Exception) {
            Log.e("주간 스케줄 목록 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    // 월간 스케줄 목록 불러오기
    suspend fun fetchScheduleMonthlyList(yearMonth: String): List<ScheduleMonthlyResponse>? {
        return try {
            scheduleService.getScheduleMonthly(yearMonth)
        } catch (e: Exception) {
            Log.e("월간 스케줄 목록 조회 응답 실패", "${e.message}", e)
            null
        }
    }

    // 다음 스케줄 불러오기
    suspend fun fetchScheduleNext(): ScheduleNextResponse? {
        return try {
            scheduleService.getScheduleNext()
        } catch (e: Exception) {
            Log.e("다음 스케줄 조회 응답 실패", "${e.message}", e)
            null
        }
    }
}
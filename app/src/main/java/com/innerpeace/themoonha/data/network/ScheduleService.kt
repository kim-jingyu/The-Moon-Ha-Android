package com.innerpeace.themoonha.data.network

import com.innerpeace.themoonha.data.model.schedule.ScheduleMonthlyResponse
import com.innerpeace.themoonha.data.model.schedule.ScheduleWeeklyResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 스케줄 서비스
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
 * </pre>
 */
interface ScheduleService {

    @GET("schedule/weekly")
    suspend fun getScheduleWeekly(@Query("standardDates") standardDates: List<String>): List<List<ScheduleWeeklyResponse>>

    @GET("schedule/monthly")
    suspend fun getScheduleMonthly(@Query("yearMonth") yearMonth: String): List<ScheduleMonthlyResponse>
}
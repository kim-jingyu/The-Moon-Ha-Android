package com.innerpeace.themoonha.adapter.schedule

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.schedule.ScheduleWeeklyResponse
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 스케줄 Adapter
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
class ScheduleWeeklyAdapter(
    private val scheduleWeeklyResponseList: List<List<ScheduleWeeklyResponse>>,
    private val standardSundayList: List<Calendar>
    ) :
    RecyclerView.Adapter<ScheduleWeeklyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleWeeklyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_one_week, parent, false)
        return ScheduleWeeklyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleWeeklyViewHolder, position: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sundayDateStrings = standardSundayList.map { dateFormat.format(it.time) }
        Log.d("adapter", scheduleWeeklyResponseList.toString())
        Log.d("adapter", sundayDateStrings.toString())
        Log.d("adapter", position.toString())
        holder.bind(scheduleWeeklyResponseList[position], standardSundayList[position])

    }

    override fun getItemCount(): Int = scheduleWeeklyResponseList.size
}




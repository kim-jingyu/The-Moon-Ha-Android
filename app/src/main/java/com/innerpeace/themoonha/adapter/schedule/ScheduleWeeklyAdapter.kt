package com.innerpeace.themoonha.adapter.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.schedule.ScheduleWeeklyResponse
import com.innerpeace.themoonha.viewModel.LoungeViewModel
import java.util.Calendar

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
    private val scheduleWeeklyResponseList: List<ScheduleWeeklyResponse>,
    private val standardSundayList: Calendar,
    private val fragment: Fragment,
    private val viewModel: LoungeViewModel
    ) :
    RecyclerView.Adapter<ScheduleWeeklyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleWeeklyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_one_week, parent, false)
        return ScheduleWeeklyViewHolder(view, fragment, viewModel)
    }

    override fun onBindViewHolder(holder: ScheduleWeeklyViewHolder, position: Int) {
        holder.bind(scheduleWeeklyResponseList, standardSundayList)

    }

    override fun getItemCount(): Int = scheduleWeeklyResponseList.size
}




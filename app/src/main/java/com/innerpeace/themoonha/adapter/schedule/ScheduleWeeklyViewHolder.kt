package com.innerpeace.themoonha.adapter.schedule

import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.schedule.ScheduleWeeklyResponse
import com.innerpeace.themoonha.databinding.DialogLessonInfoBinding
import com.innerpeace.themoonha.ui.util.Colors
import com.innerpeace.themoonha.viewModel.LoungeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * 스케줄 viewHolder
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
class ScheduleWeeklyViewHolder(view: View, private val fragment: Fragment, private val viewModel: LoungeViewModel) : RecyclerView.ViewHolder(view) {
    private val gridLayout: GridLayout = view.findViewById(R.id.gl_time_table)

    // 각 TextView에 해당하는 날짜를 할당
    private val tvDays: List<TextView> = listOf(
        view.findViewById(R.id.tv_1),
        view.findViewById(R.id.tv_2),
        view.findViewById(R.id.tv_3),
        view.findViewById(R.id.tv_4),
        view.findViewById(R.id.tv_5),
        view.findViewById(R.id.tv_6),
        view.findViewById(R.id.tv_7)
    )

    fun bind(scheduleWeeklyResponseList: List<ScheduleWeeklyResponse>, standardSunday: Calendar) {

        // 수업이 없으면 17:00 이후부터 삭제
        if (scheduleWeeklyResponseList.isEmpty()) {
            removeCellsAfterLastClass(15, 0)
            return
        }

        // 날짜 넣기
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

        for (i in tvDays.indices) {
            val dayCalendar = standardSunday.clone() as Calendar
            dayCalendar.add(Calendar.DAY_OF_YEAR, i)

            tvDays[i].text = dateFormat.format(dayCalendar.time)
        }


        var latestHour = 0
        var latestMinute = 0

        // 스케줄 표 그리기
        for (schedule in scheduleWeeklyResponseList) {
            val day = schedule.day
            val beforeHour = schedule.startHour
            val beforeMinute = schedule.startMinute
            val afterHour = schedule.endHour
            val afterMinute = schedule.endMinute

            // 날짜 계산
            val dayOffset = dayToNum(day) - 1
            val dayCalendar = standardSunday.clone() as Calendar
            dayCalendar.add(Calendar.DAY_OF_YEAR, dayOffset)


            // 가장 늦게 끝나는 수업 시간 계싼
            if (afterHour > latestHour || (afterHour == latestHour && afterMinute > latestMinute)) {
                latestHour = afterHour
                latestMinute = afterMinute
            }

            // 병합할 row 수 (강좌 지속 시간)
            val hourSubtract = (afterHour - beforeHour) * 6
            val minuteSubtract = (afterMinute - beforeMinute) / 10
            val result = hourSubtract + minuteSubtract

            // 병합 시작할 row
            val row = 1 + (beforeHour - 10) * 6 + (beforeMinute / 10)

            // 병합할 셀 제거
            removeMergedCells(day, beforeHour, beforeMinute, result, itemView)

            // 셀 병합, 수업 이름 적용
            mergeCells(schedule, day, beforeHour, beforeMinute, afterHour, afterMinute, result, row, schedule.title, dayCalendar, itemView)

        }

        // 가장 늦게 끝나는 수업이 17:00 이전이면 17:00부터 삭제
        if (latestHour < 15 || (latestHour == 15 && latestMinute == 0)) {
            removeCellsAfterLastClass(15, 0)
        }
        // 가장 늦게 끝나는 수업이 17:00 이후에 끝난다면, 해당 수업 종료시간 + 1의 00분부터 삭제
        else {
            removeCellsAfterLastClass(latestHour + 1, 0)
        }

    }

    private fun removeCellsAfterLastClass(latestHour: Int, latestMinute: Int) {

        // 요일
        val days =
            listOf("", "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday")

        // 삭제할 시간 계산
        var hour = latestHour
        var minute = latestMinute

        while (hour < 20 || (hour == 19 && minute <= 50)) {
            for (day in days) {
                val deleteCellName =
                    day + String.format("%02d", hour) + String.format("%02d", minute)
                val deleteCell = gridLayout.findViewWithTag<TextView>(deleteCellName)

                if (deleteCell != null) {
                    gridLayout.removeView(deleteCell)
                }
            }

            // 10분 단위로 시간 증가
            if (minute == 50) {
                minute = 0
                hour += 1
            } else {
                minute += 10
            }
        }
    }


    // 병합된 셀 제거
    private fun removeMergedCells(
        day: String, beforeHour: Int, beforeMinute: Int, result: Int, view: View
    ) {
        var hour = beforeHour
        var minute = beforeMinute

        for (j in 0 until result - 1) {
            val deleteCellHour = if (minute == 50) {
                if (hour + 1 < 10) "0${hour + 1}" else "${hour + 1}"
            } else {
                if (hour < 10) "0$hour" else "$hour"
            }

            val deleteCellMinute = if (minute == 50) "00" else String.format("%02d", minute + 10)
            minute = if (minute == 50) 0 else minute + 10
            hour = if (minute == 0) hour + 1 else hour

            val deleteCellName = dayToEnglish(day) + deleteCellHour + deleteCellMinute
            val deleteCell = view.findViewWithTag<TextView>(deleteCellName)

            // 셀 삭제
            if (deleteCell != null) {
                gridLayout.removeView(deleteCell)
            }
        }
    }

    // 셀 병합
    private fun mergeCells(
        schedule: ScheduleWeeklyResponse, day: String, beforeHour: Int, beforeMinute: Int, afterHour: Int, afterMinute: Int, result: Int, row: Int, title: String, date: Calendar, view: View
    ) {
        // 병합할 셀 정보
        val IDofSpanCell = dayToEnglish(day) + String.format("%02d", beforeHour) + String.format("%02d", beforeMinute)
        val spanCell = view.findViewWithTag<TextView>(IDofSpanCell)
        val layoutParams = spanCell.layoutParams as GridLayout.LayoutParams
        layoutParams.columnSpec = GridLayout.spec(dayToNum(day))
        layoutParams.rowSpec = GridLayout.spec(row, result)
        spanCell.layoutParams = layoutParams

        // 셀 병합
        layoutParams.setGravity(Gravity.FILL)
        spanCell.layoutParams = layoutParams

        // 셀 꾸미기
        // 들어갈 텍스트
        val shortenedTitle = if (title.length > 20) title.substring(0, 17) + "..." else title
        val fullText = shortenedTitle

        // 텍스트 속성
        // 제목
        spanCell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        spanCell.typeface = ResourcesCompat.getFont(itemView.context, R.font.happiness_sans_bold)


        // 시간
        val spannable = SpannableString(fullText)
        val timeStart = fullText.indexOf("\n") + 1
        spannable.setSpan(RelativeSizeSpan(0.8f), timeStart, fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spanCell.text = spannable

        // 셀 속성
        val colorIndex = schedule.lessonId.toInt() % Colors.fixedColors.size
        val color = Colors.fixedColors[colorIndex]
        spanCell.setBackgroundColor(color)
        spanCell.setPadding(12, 12, 12, 12)

        // 강좌 정보 다이얼로그
        spanCell.setOnClickListener {
            showLessonDialog(listOf(schedule), date, fragment, viewModel)
        }

    }

    private fun dayToNum(day: String): Int {
        return when (day) {
            "일" -> 1
            "월" -> 2
            "화" -> 3
            "수" -> 4
            "목" -> 5
            "금" -> 6
            "토" -> 7
            else -> 0
        }
    }

    private fun dayToEnglish(day: String): String {
        return when (day) {
            "월" -> "monday"
            "화" -> "tuesday"
            "수" -> "wednesday"
            "목" -> "thursday"
            "금" -> "friday"
            "토" -> "saturday"
            "일" -> "sunday"
            else -> ""
        }
    }

    // 클릭 시 다이얼로그
    private fun showLessonDialog(scheduleList: List<ScheduleWeeklyResponse>, date: Calendar, fragment: Fragment, viewModel: LoungeViewModel) {

        val bottomSheetDialog = BottomSheetDialog(itemView.context)
        val dialogBinding = DialogLessonInfoBinding.inflate(LayoutInflater.from(itemView.context))

        // ViewPager에 어댑터 설정
        val dialogAdapter = ScheduleWeeklyDialogAdapter(scheduleList, fragment, viewModel)
        dialogBinding.vpLessonOfDay.adapter = dialogAdapter

        // 날짜 넣기
        val formatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        dialogBinding.tvSelectedDate.text = "${formatter.format(date.time)}의 강좌"

        // BottomSheetDialog에 View 설정
        bottomSheetDialog.setContentView(dialogBinding.root)

        // 네비게이션 변경 시 다이얼로그 닫기
        val navController = fragment.findNavController()
        navController.addOnDestinationChangedListener { _, _, _ ->
            if (bottomSheetDialog.isShowing) {
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.show()
    }

}
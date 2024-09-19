package com.innerpeace.themoonha.adapter.schedule

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
import kotlin.random.Random

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
    private val gridInclude: View = view.findViewById(R.id.time_table)
    private val tvNoLesson: TextView = view.findViewById(R.id.tv_no_lesson)

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

        if (scheduleWeeklyResponseList.isEmpty()) {
            gridInclude.visibility = View.GONE
            tvNoLesson.visibility = View.VISIBLE
            return
        }
        Log.d("뷰홀더", scheduleWeeklyResponseList.toString())

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedReferenceSunday = dateFormat.format(standardSunday.time)
        Log.d("123d123", "Reference Sunday: $formattedReferenceSunday")


        // 날짜 넣기
        setupDates(standardSunday)

        var latestHour = 0
        var latestMinute = 0
        var earliestHour = 23
        var earliestMinute = 59
        var earliestDay = ""

        // 스케줄 표 그리기
        for (schedule in scheduleWeeklyResponseList) {
            val day = schedule.day
            val beforeHour = schedule.startHour
            val beforeMinute = schedule.startMinute
            val afterHour = schedule.endHour
            val afterMinute = schedule.endMinute

            // 가장 빨리 시작하는 수업 시간 갱신
            if (beforeHour < earliestHour || (beforeHour == earliestHour && beforeMinute < earliestMinute)) {
                earliestHour = beforeHour
                earliestMinute = beforeMinute
                earliestDay = day
            }


            // 가장 늦은 시간 갱신
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
            mergeCells(schedule, day, beforeHour, beforeMinute, afterHour, afterMinute, result, row, schedule.title, itemView)

        }

        // 가장 늦게 끝나는 수업 이후의 셀 제거
        removeCellsAfterLastClass(latestHour, latestMinute)

        // 가장 일찍 시작하는 수업 이전의 셀 제거
        removeCellsBeforeFirstClass(earliestHour, earliestMinute, earliestDay)

        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)

            // TextView인지 확인
            if (child is TextView) {
                val tag = child.tag

                // tag가 존재할 경우 출력
                if (tag != null) {
                    Log.d("RemainingTag", tag.toString())
                }
            }
        }

    }

    private fun removeCellsBeforeFirstClass(earliestHour: Int, earliestMinute: Int, earliestDay: String) {
        val days = listOf("", "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday")

        // 삭제 시작 시간 설정 (10:00부터 시작)
        var hour = 10
        var minute = 0

        // 삭제 범위: earliestHour의 1시간 전 50분까지
        val deleteUntilHour = earliestHour - 1
        val deleteUntilMinute = 50

        while (hour < deleteUntilHour || (hour == deleteUntilHour && minute <= deleteUntilMinute)) {
            for (day in days) {
                val deleteCellName = day + String.format("%02d", hour) + String.format("%02d", minute)
                val deleteCell = gridLayout.findViewWithTag<TextView>(deleteCellName)

                if (deleteCell != null) {
                    gridLayout.removeView(deleteCell)
                }
            }

            // 시간 증가
            if (minute == 50) {
                minute = 0
                hour += 1
            } else {
                minute += 10
            }
        }

        // 배경 변경: 00분 셀만 변경, 가장 빠른 수업이 시작하는 요일은 제외
        val remainingHour = earliestHour
        val remainingMinute = 0

        for (day in days) {
            if (day.isNotEmpty()) {
                val updateCellName = day + String.format("%02d", remainingHour) + String.format("%02d", remainingMinute)
                val updateCell = gridLayout.findViewWithTag<TextView>(updateCellName)

                if (updateCell != null) {
                    // 가장 빨리 시작하는 요일은 배경 설정 제외
                    if (!(earliestMinute == 0 && day == dayToEnglish(earliestDay))) {
                        updateCell.setBackgroundResource(R.drawable.line_right_top)
                    }
                }
            }
        }
    }


    private fun removeCellsAfterLastClass(latestHour: Int, latestMinute: Int) {
        Log.d("확인", latestHour.toString() + latestMinute.toString())

        // 요일 배열: 일요일부터 토요일까지
        val days = listOf("", "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday")

        // 첫 삭제 시간을 계산
        var hour = latestHour
        var minute = when (latestMinute) {
            in 0..49 -> 0
            else -> 0
        }

        if (latestMinute > 0) {
            hour += 1
        }

        while (hour < 20 || (hour == 19 && minute <= 50)) {
            // 요일별로 해당 시간대 셀 삭제
            for (day in days) {
                val deleteCellName = day + String.format("%02d", hour) + String.format("%02d", minute)
                val deleteCell = gridLayout.findViewWithTag<TextView>(deleteCellName)

                Log.d("삭제할 셀", deleteCellName)

                if (deleteCell != null) {
                    gridLayout.removeView(deleteCell)
                }
            }

            if (minute == 50) {
                minute = 0
                hour += 1
            } else {
                minute += 10
            }
        }
    }


    private fun setupDates(standardSunday: Calendar) {
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
        val today = Calendar.getInstance() // 오늘 날짜

        for (i in 0 until 7) {
            val currentDay = standardSunday.clone() as Calendar
            tvDays[i].text = dateFormat.format(currentDay.time)

            // 오늘 날짜와 비교하여 색상 설정
            if (currentDay.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                currentDay.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                tvDays[i].setTextColor(Color.parseColor("#01A76B"));
            } else {
                tvDays[i].setTextColor(Color.BLACK)
            }

            standardSunday.add(Calendar.DAY_OF_MONTH, 1)
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
                Log.d("병합된 셀 제거", deleteCellName)
            }
        }
    }

    // 셀 병합
    private fun mergeCells(
        schedule: ScheduleWeeklyResponse, day: String, beforeHour: Int, beforeMinute: Int, afterHour: Int, afterMinute: Int, result: Int, row: Int, title: String, view: View
    ) {
        // 병합할 셀 정보
        val IDofSpanCell = dayToEnglish(day) + String.format("%02d", beforeHour) + String.format("%02d", beforeMinute)
        val spanCell = view.findViewWithTag<TextView>(IDofSpanCell)
        val layoutParams = spanCell.layoutParams as GridLayout.LayoutParams
        layoutParams.columnSpec = GridLayout.spec(dayToNum(day))
        layoutParams.rowSpec = GridLayout.spec(row, result)
        spanCell.layoutParams = layoutParams

        Log.d("셀 병합", IDofSpanCell)


        // 셀 병합
        layoutParams.setGravity(Gravity.FILL)
        spanCell.layoutParams = layoutParams

        // 셀 꾸미기
        // 들어갈 텍스트
        val timeString = String.format("%02d:%02d~%02d:%02d", beforeHour, beforeMinute, afterHour, afterMinute)
        val shortenedTitle = if (title.length > 20) title.substring(0, 17) + "..." else title
//        val fullText = "$shortenedTitle\n\n$timeString"
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
            showLessonDialog(listOf(schedule), fragment, viewModel)
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

    private fun showLessonDialog(scheduleList: List<ScheduleWeeklyResponse>, fragment: Fragment, viewModel: LoungeViewModel) {

        val bottomSheetDialog = BottomSheetDialog(itemView.context)
        val dialogBinding = DialogLessonInfoBinding.inflate(LayoutInflater.from(itemView.context))

        // ViewPager에 어댑터 설정
        val dialogAdapter = ScheduleWeeklyDialogAdapter(scheduleList, fragment, viewModel)
        dialogBinding.vpLessonOfDay.adapter = dialogAdapter

        // 날짜 포맷 설정
        val formatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val todayDate = Calendar.getInstance().time
        dialogBinding.tvSelectedDate.text = "${formatter.format(todayDate)}의 강좌"

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
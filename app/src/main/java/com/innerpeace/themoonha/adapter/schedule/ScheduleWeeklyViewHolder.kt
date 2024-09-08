package com.innerpeace.themoonha.adapter.schedule

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.schedule.ScheduleWeeklyResponse
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
class ScheduleWeeklyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

        // 스케줄 표 그리기
        for (schedule in scheduleWeeklyResponseList) {
            val day = schedule.day
            val beforeHour = schedule.startHour
            val beforeMinute = schedule.startMinute
            val afterHour = schedule.endHour
            val afterMinute = schedule.endMinute

            // 병합할 row 수 (강좌 지속 시간)
            val hourSubtract = (afterHour - beforeHour) * 6
            val minuteSubtract = (afterMinute - beforeMinute) / 10
            val result = hourSubtract + minuteSubtract

            // 병합 시작할 row
            val row = 1 + (beforeHour - 10) * 6 + (beforeMinute / 10)

            // 병합할 셀 제거
            removeMergedCells(day, beforeHour, beforeMinute, result, itemView)

            // 셀 병합, 수업 이름 적용
            mergeCells(day, beforeHour, beforeMinute, afterHour, afterMinute, result, row, schedule.title, itemView)

        }
    }

    private fun setupDates(standardSunday: Calendar) {
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

        for (i in 0 until 7) {
            tvDays[i].text = dateFormat.format(standardSunday.time)
            standardSunday.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    // 병합된 셀 제거
    private fun removeMergedCells(
        day: String, beforeHour: Int, beforeMinute: Int, result: Int, view: View
    ) {
        var hour = beforeHour
        var minute = beforeMinute

        // ID로 병합된 셀 제거
        for (j in 0 until result - 1) {
            val deleteCellHour = if (minute == 50) {
                if (hour + 1 < 10) "0${hour + 1}" else "${hour + 1}"
            } else {
                if (hour < 10) "0$hour" else "$hour"
            }

            val deleteCellMinute = if (minute == 50) "00" else String.format("%02d", minute + 10)
            minute = if (minute == 50) 0 else minute + 10
            hour = if (minute == 0) hour + 1 else hour

            // 삭제할 셀 ID
            val deleteCellName = dayToEnglish(day) + deleteCellHour + deleteCellMinute
            val deleteCell = view.findViewWithTag<TextView>(deleteCellName)

            // 삭제
            gridLayout.removeView(deleteCell)
        }
    }

    // 셀 병합
    private fun mergeCells(
        day: String, beforeHour: Int, beforeMinute: Int, afterHour: Int, afterMinute: Int, result: Int, row: Int, title: String, view: View
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
        val randomColor = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        spanCell.setBackgroundColor(randomColor)
        spanCell.setPadding(12, 12, 12, 12)
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
}
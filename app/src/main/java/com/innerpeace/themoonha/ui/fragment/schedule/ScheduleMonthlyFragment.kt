package com.innerpeace.themoonha.ui.fragment.schedule

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.innerpeace.themoonha.adapter.schedule.ScheduleMonthlyDialogAdapter
import com.innerpeace.themoonha.data.model.schedule.ScheduleMonthlyResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LoungeService
import com.innerpeace.themoonha.data.network.ScheduleService
import com.innerpeace.themoonha.data.repository.LoungeRepository
import com.innerpeace.themoonha.data.repository.ScheduleRepository
import com.innerpeace.themoonha.databinding.DialogLessonInfoBinding
import com.innerpeace.themoonha.databinding.FragmentScheduleMonthlyBinding
import com.innerpeace.themoonha.ui.fragment.lesson.DayViewContainer
import com.innerpeace.themoonha.ui.util.Colors
import com.innerpeace.themoonha.viewModel.LoungeViewModel
import com.innerpeace.themoonha.viewModel.ScheduleViewModel
import com.innerpeace.themoonha.viewModel.factory.LoungeViewModelFactory
import com.innerpeace.themoonha.viewModel.factory.ScheduleViewModelFactory
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * 월간 스케줄 프래그먼트
 * @author 조희정
 * @since 2024.09.08
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.08  	조희정       최초 생성
 * 2024.09.08  	조희정       월간 스케줄 불러오기 기능 구현
 * </pre>
 */
class ScheduleMonthlyFragment : Fragment() {
    private var _binding: FragmentScheduleMonthlyBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    private val viewModel: ScheduleViewModel by activityViewModels {
        ScheduleViewModelFactory(
            ScheduleRepository(
                ApiClient.getClient().create(ScheduleService::class.java)
            )
        )
    }

    private val loungeViewModel: LoungeViewModel by activityViewModels {
        LoungeViewModelFactory(LoungeRepository(ApiClient.getClient().create(LoungeService::class.java)))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentMonth: YearMonth = YearMonth.now()

    // 수업별 고유 색상
    private lateinit var lessonColors: Map<Long, Int>

    // 수업 리스트
    private var scheduleMonthlyResponseList: List<ScheduleMonthlyResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleMonthlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCalendar()

        viewModel.fetchScheduleMonthlyList(currentMonth.toString())

        viewModel.scheduleMonthlyList.observe(viewLifecycleOwner) { responseList ->
            if (responseList != null) {
                scheduleMonthlyResponseList = responseList
            }
            lessonColors = assignColorsToLessons(responseList)

            binding.calendarView.notifyCalendarChanged()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendar() {
        val calendarView = binding.calendarView
        updateMonthHeader(currentMonth)

        // 드래그 방지
        calendarView.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_MOVE -> true
                else -> false
            }
        }

        calendarView.apply {
            val firstMonth = YearMonth.now().minusMonths(240)
            val lastMonth = YearMonth.now().plusMonths(240)
            val firstDayOfWeek = WeekFields.of(Locale.KOREAN).firstDayOfWeek
            setup(firstMonth, lastMonth, firstDayOfWeek)
            scrollToMonth(currentMonth)
        }

        // 이전 월 보기
        binding.previousMonthButton.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            calendarView.scrollToMonth(currentMonth)
            updateMonthHeader(currentMonth)
            viewModel.fetchScheduleMonthlyList(currentMonth.toString())
        }

        // 다음 월 보기
        binding.nextMonthButton.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            calendarView.scrollToMonth(currentMonth)
            updateMonthHeader(currentMonth)

            viewModel.fetchScheduleMonthlyList(currentMonth.toString())
        }

        // 달력에 수업 데이터 바인딩
        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view).apply {
                    // 강좌가 있는 날에만 클릭이벤트
                    view.setOnClickListener {
                        val day = this.day ?: return@setOnClickListener
                        val localDate = day.date

                        val eventsForDay = scheduleMonthlyResponseList.filter { response ->
                            val dateRange = response.period.split("~")
                            val startDate = LocalDate.parse(dateRange[0], dateFormatter)
                            val endDate = LocalDate.parse(dateRange[1], dateFormatter)

                            if (isWeeklyLesson(response.lessonTime)) {
                                !localDate.isBefore(startDate) && !localDate.isAfter(endDate) &&
                                        isLessonOnThisDay(response.lessonTime, localDate)
                            } else {
                                localDate == startDate
                            }
                        }

                        // 수업이 있으면 다이얼로그
                        if (eventsForDay.isNotEmpty()) {
                            showLessonDialog(eventsForDay, day)
                        }
                    }
                }
            }

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                val localDate = day.date
                container.day = day
                container.dayTextView.text = localDate.dayOfMonth.toString()

                if (day.owner != DayOwner.THIS_MONTH) {
                    container.dayTextView.setTextColor(Color.LTGRAY)
                } else {
                    container.dayTextView.setTextColor(Color.BLACK)
                }

                // 해당 날짜에 수업이 있는지 확인
                val eventsForDay = scheduleMonthlyResponseList.filter { response ->
                    val dateRange = response.period.split("~")
                    val startDate = LocalDate.parse(dateRange[0], dateFormatter)
                    val endDate = LocalDate.parse(dateRange[1], dateFormatter)

                    if (isWeeklyLesson(response.lessonTime)) {
                        !localDate.isBefore(startDate) && !localDate.isAfter(endDate) &&
                                isLessonOnThisDay(response.lessonTime, localDate)
                    } else {
                        localDate == startDate
                    }
                }


                // 수업이 있는 경우 색상 주기
                if (eventsForDay.isNotEmpty()) {
                    container.colorBarContainer.removeAllViews()
                    container.eventIds.clear()

                    eventsForDay.forEach { event ->
                        val color = lessonColors[event.lessonId] ?: Colors.fixedColors.random()

                        val colorBar = View(container.colorBarContainer.context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                10
                            ).apply {
                                setMargins(0, 2, 0, 2)
                            }
                            setPadding(0, 5, 0, 0)
                            setBackgroundColor(color)
                        }
                        container.colorBarContainer.addView(colorBar)
                        container.eventIds.add(event.lessonId.toString())
                    }
                    container.colorBarContainer.visibility = View.VISIBLE
                } else {
                    container.colorBarContainer.visibility = View.GONE
                }
            }
        }

        val titlesContainer = binding.titlesContainer
        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                var dayOfWeek: DayOfWeek? = null
                if (index == 0) {
                    dayOfWeek = DayOfWeek.of(7 - index)
                } else {
                    dayOfWeek = DayOfWeek.of(index)
                }

                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
                textView.text = title
            }
    }

    // 다회차 수업인지 확인
    private fun isWeeklyLesson(lessonTime: String): Boolean {
        return lessonTime.contains("매주")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isLessonOnThisDay(lessonTime: String, localDate: LocalDate): Boolean {
        val lessonDayOfWeek = lessonTime.split(" ")[1]
        val dayOfWeekMap = mapOf(
            "일" to DayOfWeek.SUNDAY,
            "월" to DayOfWeek.MONDAY,
            "화" to DayOfWeek.TUESDAY,
            "수" to DayOfWeek.WEDNESDAY,
            "목" to DayOfWeek.THURSDAY,
            "금" to DayOfWeek.FRIDAY,
            "토" to DayOfWeek.SATURDAY
        )
        val targetDayOfWeek = dayOfWeekMap[lessonDayOfWeek]
        return localDate.dayOfWeek == targetDayOfWeek
    }

    // 수업마다 색상 할당
    @RequiresApi(Build.VERSION_CODES.O)
    private fun assignColorsToLessons(responseList: List<ScheduleMonthlyResponse>?): Map<Long, Int> {
        val lessonColors = mutableMapOf<Long, Int>()

        responseList?.forEach { response ->
            if (!lessonColors.containsKey(response.lessonId)) {
                val color = generateAvailableColor(response.lessonId)
                lessonColors[response.lessonId] = color
            }
        }

        return lessonColors
    }

    // 색상 설정
    private fun generateAvailableColor(lessonId: Long): Int {
        val colorIndex = (lessonId % Colors.fixedColors.size).toInt()
        return Colors.fixedColors[colorIndex]
    }


    // 달력 날짜 헤더
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateMonthHeader(yearMonth: YearMonth) {
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월", Locale.getDefault())
        binding.currentMonthText.text = yearMonth.format(formatter)
    }

    // 강좌 상세정보 다이얼로그
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showLessonDialog(eventsForDay: List<ScheduleMonthlyResponse>, day: CalendarDay) {

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val dialogBinding = DialogLessonInfoBinding.inflate(layoutInflater)
        val dialogAdapter = ScheduleMonthlyDialogAdapter(eventsForDay, this, loungeViewModel)
        dialogBinding.vpLessonOfDay.adapter = dialogAdapter
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일", Locale.getDefault())
        dialogBinding.tvSelectedDate.text = "${day.date.format(formatter)}의 강좌"

        // 인디케이터 연결
        dialogBinding.vpScheduleDotsIndicator.setViewPager2(dialogBinding.vpLessonOfDay)

        // BottomSheetDialog에 View 설정
        bottomSheetDialog.setContentView(dialogBinding.root)

        // 네비게이션 변경 시 다이얼로그 닫기
        val navController = this.findNavController()
        navController.addOnDestinationChangedListener { _, _, _ ->
            if (bottomSheetDialog.isShowing) {
                bottomSheetDialog.dismiss()
            }
        }

        bottomSheetDialog.show()
    }

}

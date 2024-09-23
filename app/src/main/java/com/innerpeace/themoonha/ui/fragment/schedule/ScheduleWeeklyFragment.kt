package com.innerpeace.themoonha.ui.fragment.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.innerpeace.themoonha.adapter.schedule.ScheduleWeeklyAdapter
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LoungeService
import com.innerpeace.themoonha.data.network.ScheduleService
import com.innerpeace.themoonha.data.repository.LoungeRepository
import com.innerpeace.themoonha.data.repository.ScheduleRepository
import com.innerpeace.themoonha.databinding.FragmentScheduleWeeklyBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.ui.util.LoadingDialog
import com.innerpeace.themoonha.viewModel.LoungeViewModel
import com.innerpeace.themoonha.viewModel.ScheduleViewModel
import com.innerpeace.themoonha.viewModel.factory.LoungeViewModelFactory
import com.innerpeace.themoonha.viewModel.factory.ScheduleViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/**
 * 주간 스케줄 프래그먼트
 * @author 조희정
 * @since 2024.09.04
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.04  	조희정       최초 생성
 * 2024.09.07  	조희정       주간 스케줄 불러오기 기능 구현
 * </pre>
 */
class ScheduleWeeklyFragment : Fragment() {

    private var _binding: FragmentScheduleWeeklyBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ScheduleWeeklyAdapter
    private lateinit var loadingDialog: LoadingDialog

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val yearMonthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
    private var standardSundayCalendarList = mutableListOf<Calendar>()
    private var standardSundayStringList = mutableListOf<String>()


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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleWeeklyBinding.inflate(inflater, container, false)
        val view = binding.root

        // 로딩 다이얼로그
        loadingDialog = LoadingDialog(requireContext())

        // 툴바 제목 변경
        (activity as? MainActivity)?.setToolbarTitle("스케줄")

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewPager 설정
        viewPager = binding.vpWeeklyTable

        // 드래그 이동 막기
        viewPager.getChildAt(0).setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_MOVE -> true
                else -> false
            }
        }

        // 로딩 시작
        loadingDialog.show()

        standardSundayCalendarList = getWeeklyDates().toMutableList()
        standardSundayStringList = standardSundayCalendarList.map { dateFormat.format(it.time) }.toMutableList()

        // 데이터 받아오기
        viewModel.selectedStandardDate.observe(viewLifecycleOwner, Observer { standardDates ->
            standardDates?.let {
                viewModel.fetchScheduleWeeklyList(it)
            }
        })

        // 데이터 어댑터에 반영
        viewModel.scheduleWeeklyList.observe(viewLifecycleOwner, Observer { scheduleWeeklyResponseList ->
            scheduleWeeklyResponseList?.let {
                adapter = ScheduleWeeklyAdapter(it, standardSundayCalendarList, this, loungeViewModel)
                viewPager.adapter = adapter
                viewPager.post {
                    viewPager.currentItem = 1
                    updateYearMonthDisplay(standardSundayCalendarList[1])
                }
            }
        })

        // 처음 데이터 요청 (오늘 날짜)
        viewModel.setSelectedStandardDate(standardSundayStringList)

        // 다음 버튼
        binding.nextWeekButton.setOnClickListener {
            updateWeeklyDates(forward = true)
            viewModel.setSelectedStandardDate(standardSundayStringList)
        }

        // 이전 버튼
        binding.previousWeekButton.setOnClickListener {
            updateWeeklyDates(forward = false)
            viewModel.setSelectedStandardDate(standardSundayStringList)
        }

        loadingDialog.dismiss()
    }

    private fun getWeeklyDates(): List<Calendar> {
        val today = Calendar.getInstance()

        // 현재 주, 저번 주, 다음 주 일요일 구하기
        val currentSunday = getSunday(today)
        val lastSunday = getSunday((today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -7) })
        val nextSunday = getSunday((today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 7) })

        return listOf(lastSunday, currentSunday, nextSunday)
    }

    private fun getSunday(calendar: Calendar): Calendar {
        val clonedCalendar = calendar.clone() as Calendar
        val dayOfWeek = clonedCalendar.get(Calendar.DAY_OF_WEEK)
        val diff = Calendar.SUNDAY - dayOfWeek
        clonedCalendar.add(Calendar.DAY_OF_YEAR, diff)
        return clonedCalendar
    }

    // 기준일 변경
    private fun updateWeeklyDates(forward: Boolean) {
        val offset = if (forward) 7 else -7

        for (i in standardSundayCalendarList.indices) {
            standardSundayCalendarList[i].add(Calendar.DAY_OF_YEAR, offset)
        }
        standardSundayStringList = standardSundayCalendarList.map { dateFormat.format(it.time) }.toMutableList()
    }

    // 연-월 바인딩
    private fun updateYearMonthDisplay(calendar: Calendar) {
        val yearMonth = yearMonthFormat.format(calendar.time)
        binding.tvYearMonth.text = yearMonth
    }

}
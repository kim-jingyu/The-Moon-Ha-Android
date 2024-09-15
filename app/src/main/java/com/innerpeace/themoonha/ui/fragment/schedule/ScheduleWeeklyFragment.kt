package com.innerpeace.themoonha.ui.fragment.schedule

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.innerpeace.themoonha.adapter.schedule.ScheduleWeeklyAdapter
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.ScheduleService
import com.innerpeace.themoonha.data.repository.ScheduleRepository
import com.innerpeace.themoonha.databinding.FragmentScheduleWeeklyBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.ScheduleViewModel
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

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val viewModel: ScheduleViewModel by activityViewModels {
        ScheduleViewModelFactory(
            ScheduleRepository(
                ApiClient.getClient().create(ScheduleService::class.java)
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScheduleWeeklyBinding.inflate(inflater, container, false)
        val view = binding.root

        // 툴바 제목 변경
        (activity as? MainActivity)?.setToolbarTitle("스케줄")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = binding.vpWeeklyTable

        // 최초 데이터 로드 (오늘 날짜 기준으로 일요일 계산)
        val today = Calendar.getInstance()

        // 초기 일요일 날짜 리스트 설정
        val initialSundayDates = getSundayDates(today)
        updateSundayDates(initialSundayDates)

        // 기준일 변경
        viewModel.selectedStandardDate.observe(viewLifecycleOwner, Observer { standardDates ->
            standardDates?.let {
                viewModel.fetchScheduleWeeklyList(it)
            }
            val yearMonth = standardDates[1]
            binding.tvYearMonth.text = "${yearMonth.substring(0, 4)}년 ${yearMonth.substring(5, 7)}월"
        })

        // 주간 스케줄 목록 변경
        viewModel.scheduleWeeklyList.observe(viewLifecycleOwner, Observer { scheduleList ->
            scheduleList?.let {
                adapter = ScheduleWeeklyAdapter(it, initialSundayDates)
                viewPager.adapter = adapter
                viewPager.setCurrentItem(1, false)
            }
        })


        // ViewPager 위치에 따라 데이터 재로딩
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                viewModel.selectedStandardDate.value?.let { standardDates ->
                    when (position) {
                        0 -> {
                            val previousSunday = Calendar.getInstance().apply {
                                time = dateFormat.parse(standardDates[0])!!
                            }
                            val updatedSundayDates = getSundayDates(previousSunday)
                            updateSundayDates(updatedSundayDates)
                            viewPager.setCurrentItem(1, false)
                        }

                        2 -> {
                            val nextSunday = Calendar.getInstance().apply {
                                time = dateFormat.parse(standardDates[2])!!
                            }
                            val updatedSundayDates = getSundayDates(nextSunday)
                            updateSundayDates(updatedSundayDates)
                            viewPager.setCurrentItem(1, false)
                        }
                    }
                }
            }
        })
    }

    // 일요일 날짜 리스트
    private fun updateSundayDates(sundayDates: List<Calendar>) {
        val sundayDateStrings = sundayDates.map { dateFormat.format(it.time) }

        Log.d("SundayDatesStrings", sundayDateStrings.toString())
        Log.d("SundayDatesCalendars", sundayDates.toString())

        viewModel.setSelectedStandardDates(sundayDateStrings)
    }

    // 기준이 되는 일요일 구하기
    fun getSundayDates(standardDay: Calendar): List<Calendar> {
        // 이번 주 일요일
        val currentSunday = standardDay.clone() as Calendar
        val dayOfWeek = currentSunday.get(Calendar.DAY_OF_WEEK)
        val offset = (dayOfWeek - Calendar.SUNDAY + 7) % 7
        currentSunday.add(Calendar.DAY_OF_YEAR, -offset)

        // 1주일 전 일요일
        val oneWeekBeforeSunday = currentSunday.clone() as Calendar
        oneWeekBeforeSunday.add(Calendar.WEEK_OF_YEAR, -1)

        // 1주일 후 일요일
        val nextSunday = currentSunday.clone() as Calendar
        nextSunday.add(Calendar.WEEK_OF_YEAR, 1)

        return listOf(oneWeekBeforeSunday, currentSunday, nextSunday)
    }
}
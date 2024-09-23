package com.innerpeace.themoonha.ui.fragment.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LoungeService
import com.innerpeace.themoonha.data.network.ScheduleService
import com.innerpeace.themoonha.data.repository.LoungeRepository
import com.innerpeace.themoonha.data.repository.ScheduleRepository
import com.innerpeace.themoonha.databinding.FragmentScheduleBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.LoungeViewModel
import com.innerpeace.themoonha.viewModel.ScheduleViewModel
import com.innerpeace.themoonha.viewModel.factory.LoungeViewModelFactory
import com.innerpeace.themoonha.viewModel.factory.ScheduleViewModelFactory

/**
 * 스케줄 프래그먼트
 * @author 조희정
 * @since 2024.08.23
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.24  	조희정       최초 생성
 * 2024.09.09  	조희정       다음 스케줄 바인딩
 * </pre>
 */
class ScheduleFragment : Fragment() {
    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val view = binding.root

        // 툴바 제목 변경
        (activity as? MainActivity)?.setToolbarTitle("스케줄")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTabLayout()

        viewModel.scheduleNext.observe(viewLifecycleOwner) { schedule ->
            if (schedule != null) {

                binding.itemNextInfo.cardNextLesson.visibility = View.VISIBLE
                binding.itemNextInfo.tvNoLessonToday.visibility = View.GONE

                binding.itemNextInfo.tvBranchName.text = schedule.branchName
                binding.itemNextInfo.tvTitle.text = schedule.lessonTitle
                binding.itemNextInfo.tvCnt.text = "${schedule.cnt}회"
                binding.itemNextInfo.tvTutorName.text = schedule.tutorName
                binding.itemNextInfo.tvLessonTime.text = schedule.lessonTime

                 //loungeId가 있을 때 버튼을 보여주고, 없으면 숨김
                if (schedule.loungeId == null || schedule.loungeId == 0L) {
                    binding.itemNextInfo.btnApplyBtn.visibility = View.INVISIBLE
                } else {
                    binding.itemNextInfo.btnApplyBtn.visibility = View.VISIBLE
                    binding.itemNextInfo.btnApplyBtn.setOnClickListener {
                        navigateToLounge(schedule.loungeId)
                    }
                }
            } else {
                binding.itemNextInfo.cardNextLesson.visibility = View.GONE
                binding.itemNextInfo.tvNoLessonToday.visibility = View.VISIBLE
            }

        }

        viewModel.fetchScheduleNext()
    }

    private var currentTab: TextView? = null

    private fun setTabLayout() {
        // 초기화면
        replaceFragment(ScheduleWeeklyFragment())
        setActiveTab(binding.tvWeeklyView)

        // 주간 보기
        binding.tvWeeklyView.setOnClickListener {
            if (currentTab != binding.tvWeeklyView) {
                replaceFragment(ScheduleWeeklyFragment())
                setActiveTab(binding.tvWeeklyView)
            }
        }

        // 월별 보기
        binding.tvMonthlyView.setOnClickListener {
            if (currentTab != binding.tvMonthlyView) {
                replaceFragment(ScheduleMonthlyFragment())
                setActiveTab(binding.tvMonthlyView)
            }
        }
    }

    // 탭 페이지 이동
    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.tab_layout_fragment, fragment)
            .commit()
    }

    // 선택된 탭 색상
    private fun setActiveTab(selectedTextView: TextView) {
        binding.tvWeeklyView.setTextColor(ContextCompat.getColor(requireContext(), R.color.silver))
        binding.tvMonthlyView.setTextColor(ContextCompat.getColor(requireContext(), R.color.silver))

        selectedTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

        currentTab = selectedTextView
    }

    private fun navigateToLounge(loungeId: Long) {
        loungeViewModel.setSelectedLoungeId(loungeId)
        findNavController().navigate(R.id.action_fragment_schedule_to_loungeHomeFragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
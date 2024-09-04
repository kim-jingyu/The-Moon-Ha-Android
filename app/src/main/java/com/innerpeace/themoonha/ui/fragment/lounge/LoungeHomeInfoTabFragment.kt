package com.innerpeace.themoonha.ui.fragment.lounge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.lounge.LoungeHomeMemberViewAdapter
import com.innerpeace.themoonha.adapter.lounge.LoungeHomePostViewAdapter
import com.innerpeace.themoonha.adapter.lounge.item.SharedViewModel
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LoungeService
import com.innerpeace.themoonha.data.repository.LoungeRepository
import com.innerpeace.themoonha.databinding.FragmentLoungeHomeInfoTabBinding
import com.innerpeace.themoonha.ui.ConditionalScrollLayoutManager
import com.innerpeace.themoonha.viewModel.LoungeViewModel
import com.innerpeace.themoonha.viewModel.factory.LoungeViewModelFactory

/**
 * 라운지 강좌 정보 탭 프레그먼트
 * @author 조희정
 * @since 2024.09.01
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.01  	조희정       최초 생성
 * 2024.09.01   조희정       강사 정보, 회원목록 recycler view 구현
 * </pre>
 */
class LoungeHomeInfoTabFragment : Fragment() {
    private var _binding: FragmentLoungeHomeInfoTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var layoutManager: ConditionalScrollLayoutManager

    private lateinit var adapter: LoungeHomeMemberViewAdapter
    private val viewModel: LoungeViewModel by activityViewModels {
        LoungeViewModelFactory(LoungeRepository(ApiClient.getClient().create(LoungeService::class.java)))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoungeHomeInfoTabBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 스크롤 Control
        layoutManager = ConditionalScrollLayoutManager(context)
        binding.rvMemberList.layoutManager = layoutManager

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.isScrollEnabled.observe(viewLifecycleOwner, { isEnabled ->
            layoutManager.setScrollEnabled(isEnabled)
        })

        // 출석 토글
        binding.llToggleAttendance.setOnClickListener {
            toggleVisibility(binding.tableAttendance.root, binding.ivAttendanceArrow)
        }

        // 강좌 계획서 토글
        binding.llTogglePlan.setOnClickListener {
            toggleVisibility(binding.tvPlanDetail, binding.ivPlanArrow)
        }

        // 참여자 목록 토글
        binding.llToggleMember.setOnClickListener {
            toggleVisibility(binding.rvMemberList, binding.ivMemberArrow)
        }

        viewModel.loungeHome.observe(viewLifecycleOwner, Observer { home ->
            if (home != null) {
                Glide.with(binding.ivProfileImage.context)
                    .load(home.loungeInfo.tutorImgUrl)
                    .circleCrop()
                    .into(binding.ivProfileImage)
                binding.tvTutorName.text = home.loungeInfo.tutorName
                setupAttendance(home.attendanceList)
                binding.tvPlanDetail.text = home.loungeInfo.summary
                setupMemberRecyclerView(home.loungeMemberList)
            }
        })
    }

    // 토글 기능
    private fun toggleVisibility(view: View, arrow: ImageView) {
        if (view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
            arrow.setImageResource(R.drawable.ic_arrow_down)
        } else {
            view.visibility = View.VISIBLE
            arrow.setImageResource(R.drawable.ic_arrow_up)
        }
    }

    // 출석 정보
    private fun setupAttendance(item: List<LoungeHomeResponse.Attendance>) {

    }

    // 회원 목록 Recycler view
    private fun setupMemberRecyclerView(item: List<LoungeHomeResponse.LoungeMember>) {
        adapter = LoungeHomeMemberViewAdapter { loungeItem ->
            navigateToDetailFragment(loungeItem)
        }
        binding.rvMemberList.adapter = adapter

        adapter.setItems(item)
    }

    // 회원 페이지로 이동
    private fun navigateToDetailFragment(item: LoungeHomeResponse.LoungeMember) {
        viewModel.setSelectedMemberId(item.memberId)
//        findNavController().navigate(R.id.action_loungeHomeFragment_to_loungePostFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
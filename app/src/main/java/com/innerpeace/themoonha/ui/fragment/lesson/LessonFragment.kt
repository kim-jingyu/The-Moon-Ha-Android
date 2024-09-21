package com.innerpeace.themoonha.ui.fragment.lesson

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.lesson.LessonAdapter
import com.innerpeace.themoonha.adapter.lesson.ShortFormAdapter
import com.innerpeace.themoonha.data.model.lesson.Branch
import com.innerpeace.themoonha.data.model.lesson.CartRequest
import com.innerpeace.themoonha.data.model.lesson.LessonListRequest
import com.innerpeace.themoonha.data.model.lesson.toQueryMap
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LessonService
import com.innerpeace.themoonha.data.repository.LessonRepository
import com.innerpeace.themoonha.databinding.FragmentLessonBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.LessonViewModel
import com.innerpeace.themoonha.viewModel.factory.LessonViewModelFactory

/**
 * 강좌 목록 조회 페이지 프레그먼트
 *
 * @author 손승완
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	손승완       최초 생성
 * </pre>
 * @since 2024.08.31
 */
class LessonFragment : Fragment() {
    private var _binding: FragmentLessonBinding? = null
    private val binding get() = _binding!!
    private lateinit var lessonAdapter: LessonAdapter
    private lateinit var shortFormAdapter: ShortFormAdapter
    private var selectedBranch: Branch? = null
    private val viewModel: LessonViewModel by activityViewModels {
        LessonViewModelFactory(LessonRepository(ApiClient.getClient().create(LessonService::class.java)))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val queryMap = LessonListRequest(
            branchId = selectedBranch?.branchId?.toString(),
            categoryId = null,
            lessonTitle = null,
            tutorName = null,
            day = null,
            target = null,
            cnt = null,
            lessonTime = null
        ).toQueryMap()
        viewModel.getLessonList(queryMap)
        _binding = FragmentLessonBinding.inflate(inflater, container, false)
        val view = binding.root

        activity?.findViewById<Toolbar>(R.id.toolbar)?.visibility = View.VISIBLE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.visibility = View.VISIBLE

        // 툴바 제목 변경
        (activity as? MainActivity)?.setToolbarTitle("문화센터")

        binding.linearLayout1.setOnClickListener {
            showBranchSelectionDialog()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewLesson.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        lessonAdapter = LessonAdapter(lessons = listOf(), onLessonClick = { lessonId ->
            findNavController().navigate(R.id.action_fragment_lesson_to_lessonDetailFragment, bundleOf("lessonId" to lessonId)  )
        }, onAddToCartClick = { lessonId ->
            val cartRequest = CartRequest(
                lessonId = lessonId,
                onlineYn = false
            )

            viewModel.addLessonCart(cartRequest).observe(viewLifecycleOwner, Observer { success ->
                if (success) {
                    findNavController().navigate(R.id.action_fragment_lesson_to_cartContentFragment)
                } else {
                    Log.e("LessonFragment", "장바구니에 상품 추가 실패")
                }
            })
        })
        binding.recyclerViewLesson.adapter = lessonAdapter

        shortFormAdapter = ShortFormAdapter(emptyList()) { shortForm, position ->
            val bundle = bundleOf("selectedPosition" to position)
            viewModel.currentPage = position
            findNavController().navigate(R.id.action_fragment_lesson_to_shortFormDetailFragment, bundle)
        }

        binding.craftButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_lesson_to_craftFragment)
        }

        binding.liveButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_lesson_to_liveFragment)
        }

        binding.recyclerViewShortForm.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewShortForm.adapter = shortFormAdapter

        binding.myCultureCenter.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_lesson_to_cartContentFragment)
        }

        // ViewModel의 데이터 관찰
        viewModel.lessonList.observe(viewLifecycleOwner, Observer { lessons ->
            lessonAdapter.updateLessons(lessons)
        })

        viewModel.shortFormList.observe(viewLifecycleOwner, Observer { shortForms ->
            shortFormAdapter.updateShortForms(shortForms)
        })

        viewModel.memberName.observe(viewLifecycleOwner, Observer { memberName ->
            binding.textViewMemberName.text = "${memberName}님을 위한 추천 강좌"
        })

        viewModel.branchName.observe(viewLifecycleOwner, Observer { branchName ->
            binding.branchName.text = branchName
        })
    }

    private fun showBranchSelectionDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.branch_selection_dialog, null)
        bottomSheetDialog.setContentView(dialogView)

        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        BottomSheetBehavior.from(bottomSheet!!).skipCollapsed = false
        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED

        val branchButtonsMap = mapOf(
            Branch.TRADE_CENTER to dialogView.findViewById<Button>(R.id.branchTradeCenter),
            Branch.CHEONHO to dialogView.findViewById<Button>(R.id.branchCheonho),
            Branch.SINCHON to dialogView.findViewById<Button>(R.id.branchSinchon),
            Branch.MIA to dialogView.findViewById<Button>(R.id.branchMia),
            Branch.MOKDONG to dialogView.findViewById<Button>(R.id.branchMokdong),
            Branch.KINTEX to dialogView.findViewById<Button>(R.id.branchKintex),
            Branch.DCUBE_CITY to dialogView.findViewById<Button>(R.id.branchDcubeCity),
            Branch.PANGYO to dialogView.findViewById<Button>(R.id.branchPangyo),
            Branch.HYUNDAI_SEOUL to dialogView.findViewById<Button>(R.id.branchHyundaiSeoul),
            Branch.JUNG_DONG to dialogView.findViewById<Button>(R.id.branchJungdong),
            Branch.APGUJEONG to dialogView.findViewById<Button>(R.id.branchApgujeong),
            Branch.HYUNDAI_DAEGU to dialogView.findViewById<Button>(R.id.branchHyundaiDaegu)
        )

        val btnCompleteSelection = dialogView.findViewById<Button>(R.id.btnCompleteSelection)
        btnCompleteSelection.setBackgroundColor(resources.getColor(R.color.gray))
        btnCompleteSelection.isEnabled = false

        branchButtonsMap.forEach { (branch, button) ->
            button?.setOnClickListener {
                // 다른 버튼은 선택 해제 및 기본 서체로 변경
                branchButtonsMap.values.forEach {
                    it.setTypeface(null, Typeface.NORMAL)
                    it.isSelected = false
                }

                // 선택된 버튼을 굵게 표시하고 선택된 상태로 설정
                button.isSelected = true
                button.setTypeface(null, Typeface.BOLD)

                selectedBranch = branch

                // 제출 버튼 활성화 및 색상 변경
                btnCompleteSelection.isEnabled = true
                btnCompleteSelection.setBackgroundColor(resources.getColor(R.color.black))
            }
        }

        btnCompleteSelection.setOnClickListener {
            selectedBranch?.let {
                val newQueryMap = LessonListRequest(
                    branchId = it.branchId.toString(),
                    categoryId = null,
                    lessonTitle = null,
                    tutorName = null,
                    day = null,
                    target = null,
                    cnt = null,
                    lessonTime = null
                ).toQueryMap()

                viewModel.getLessonList(newQueryMap)
                viewModel.updateBranchName(it.branchName)
                bottomSheetDialog.dismiss()
            }
        }

        // 바텀 시트 다이얼로그 표시
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

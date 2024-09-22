package com.innerpeace.themoonha.ui.fragment.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.lesson.ShortFormDetailAdapter
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LessonService
import com.innerpeace.themoonha.data.repository.LessonRepository
import com.innerpeace.themoonha.databinding.FragmentShortFormDetailBinding
import com.innerpeace.themoonha.viewModel.LessonViewModel
import com.innerpeace.themoonha.viewModel.factory.LessonViewModelFactory

/**
 * 숏폼 상세 조회 페이지 프레그먼트
 *
 * @author 손승완
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.01  	손승완       최초 생성
 * </pre>
 * @since 2024.09.01
 */
class ShortFormDetailFragment : Fragment() {

    private var _binding: FragmentShortFormDetailBinding? = null
    val binding get() = _binding!!
    private lateinit var adapter: ShortFormDetailAdapter
    private val viewModel: LessonViewModel by activityViewModels {
        LessonViewModelFactory(LessonRepository(ApiClient.getClient().create(LessonService::class.java)))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShortFormDetailBinding.inflate(inflater, container, false)
        activity?.findViewById<Toolbar>(R.id.toolbar)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = binding.viewPager2

        viewModel.currentShortFormId.value?.let { viewModel.getShortFormDetail(it) }

        viewModel.shortFormList.observe(viewLifecycleOwner) { shortForms ->
            adapter = ShortFormDetailAdapter(shortForms, viewModel)
            binding.viewPager2.adapter = adapter

            val selectedPosition = viewModel.currentPage // 여기서 현재 페이지를 가져옴
            viewPager.setCurrentItem(selectedPosition, false)

            binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    val shortForm = adapter.getShortFormAt(position)
                    shortForm?.let {
                        viewModel.getShortFormDetail(it.shortFormId)
                    }
                }
            })

            binding.leftArrow.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val currentPage = viewModel.currentPage
        binding.viewPager2.setCurrentItem(currentPage, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

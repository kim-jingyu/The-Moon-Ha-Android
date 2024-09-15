package com.innerpeace.themoonha.ui.fragment.beforeafter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.innerpeace.themoonha.adapter.bite.BeforeAfterDetailAdapter
import com.innerpeace.themoonha.data.repository.BeforeAfterRepository
import com.innerpeace.themoonha.databinding.FragmentBeforeAfterDetailBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.BeforeAfterViewModel
import com.innerpeace.themoonha.viewModel.factory.BeforeAfterViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Before&After Detail 프래그먼트
 * @author 김진규
 * @since 2024.08.26
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.26  	김진규       최초 생성
 * </pre>
 */
class BeforeAfterDetailFragment : Fragment() {
    private var _binding: FragmentBeforeAfterDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BeforeAfterDetailAdapter
    private val viewModel: BeforeAfterViewModel by activityViewModels {
        BeforeAfterViewModelFactory(BeforeAfterRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBeforeAfterDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.apply {
            hideToolbar()
            hideBottomNavigation()
        }

        binding.backButton.setColorFilter(Color.WHITE)

        val viewPager = binding.viewPager2
        val selectedPosition = arguments?.getInt("selectedPosition") ?: 0
        val sortOption = arguments?.getInt("sortOption") ?: 0

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        if (sortOption == 0) {
            getDetailsByLatest(selectedPosition, viewPager)
        } else if (sortOption == 1) {
            getDetailsByTitle(selectedPosition, viewPager)
        }
    }

    private fun getDetailsByTitle(
        selectedPosition: Int,
        viewPager: ViewPager2
    ) {
        viewModel.getBeforeAfterDetailsByTitle(selectedPosition)
        lifecycleScope.launch {
            viewModel.beforeAfterDetailByTitleResponse.collect { details ->
                adapter = BeforeAfterDetailAdapter(details)
                viewPager.adapter = adapter
                viewPager.setCurrentItem(0, false)
            }
        }
    }

    private fun getDetailsByLatest(
        selectedPosition: Int,
        viewPager: ViewPager2
    ) {
        viewModel.getBeforeAfterDetailsByLatest(selectedPosition)
        lifecycleScope.launch {
            viewModel.beforeAfterDetailByLatestResponse.collect { details ->
                adapter = BeforeAfterDetailAdapter(details)
                viewPager.adapter = adapter
                viewPager.setCurrentItem(0, false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


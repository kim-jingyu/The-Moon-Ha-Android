package com.innerpeace.themoonha.ui.fragment.field

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.innerpeace.themoonha.adapter.bite.FieldDetailAdapter
import com.innerpeace.themoonha.data.repository.FieldRepository
import com.innerpeace.themoonha.databinding.FragmentFieldDetailBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.FieldViewModel
import com.innerpeace.themoonha.viewModel.factory.FieldViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Field Detail 프래그먼트
 * @author 김진규
 * @since 2024.09.05
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.05  	김진규       최초 생성
 * </pre>
 */
class FieldDetailFragment : Fragment() {
    private var _binding: FragmentFieldDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FieldDetailAdapter
    private val viewModel: FieldViewModel by activityViewModels {
        FieldViewModelFactory(FieldRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFieldDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.apply {
            hideToolbar()
            hideBottomNavigation()
        }

        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        val viewPager = binding.viewPager2
        val selectedPosition = arguments?.getInt("selectedPosition") ?: 0
        val sortOption = arguments?.getInt("sortOption") ?: 0

        if (sortOption == 0) {
            viewModel.getFieldDetailsByLatest(selectedPosition)
            lifecycleScope.launch {
                viewModel.fieldDetailByLatestResponses.collect { details ->
                    adapter = FieldDetailAdapter(details)
                    viewPager.adapter = adapter
                    viewPager.setCurrentItem(0, false)
                }
            }
        } else if (sortOption == 1) {
            viewModel.getFieldDetailsByTitle(selectedPosition)
            lifecycleScope.launch {
                viewModel.fieldDetailByTitleResponses.collect { details ->
                    adapter = FieldDetailAdapter(details)
                    viewPager.adapter = adapter
                    viewPager.setCurrentItem(0, false)
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
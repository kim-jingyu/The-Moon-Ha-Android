package com.innerpeace.themoonha.ui.fragment.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.live.LiveOnAirListAdapter
import com.innerpeace.themoonha.data.model.live.LiveLessonResponse
import com.innerpeace.themoonha.data.repository.LiveRepository
import com.innerpeace.themoonha.databinding.FragmentLiveOnAirListBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.LiveViewModel
import com.innerpeace.themoonha.viewModel.factory.LiveViewModelFactory

/**
 * 실시간 강좌 - OnAir 목록 조회 프래그먼트
 * @author 김진규
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	김진규       최초 생성
 * </pre>
 */
class LiveOnAirListFragment : Fragment() {
    private var _binding: FragmentLiveOnAirListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LiveOnAirListAdapter
    private val viewModel: LiveViewModel by viewModels {
        LiveViewModelFactory(LiveRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLiveOnAirListBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as? MainActivity)?.apply {
            setToolbarTitle("실시간 강좌")
            showToolbar()
            showBottomNavigation()
        }

        setHasOptionsMenu(true)
        setupToMyLiveLesson()
        setupRecyclerView()
        setupSpinner()

        viewModel.liveLessonListResponse.asLiveData().observe(viewLifecycleOwner) { contents ->
            adapter.updateContents(contents)
        }

        return view
    }

    private fun setupSpinner() {
        val sortOptions = arrayOf("최신순", "제목순")
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sort.adapter = arrayAdapter

        binding.sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        viewModel.getLiveLessonListWithoutMember()
                    }
                    1 -> {
                        viewModel.getLiveLessonListWithoutMemberOrderByTitle()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.getLiveLessonListWithoutMember()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.fieldListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = LiveOnAirListAdapter(emptyList()) { content ->
            navigateToLiveLessonStreamingMain(content)
        }
        binding.fieldListRecyclerView.adapter = adapter
    }

    private fun navigateToLiveLessonStreamingMain(content: LiveLessonResponse) {
        viewModel.getLiveLessonDetail(content.liveId)
        viewModel.liveLessonDetailResponse.asLiveData().observe(viewLifecycleOwner) { detailResponse ->
            detailResponse?.let {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, LiveStreamingMainFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable("liveLessonDetailResponse", it)
                        }
                    })
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun setupToMyLiveLesson() {
        binding.myLiveLesson.setOnClickListener {
            findNavController().navigate(R.id.action_onAir_to_myLiveLesson)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
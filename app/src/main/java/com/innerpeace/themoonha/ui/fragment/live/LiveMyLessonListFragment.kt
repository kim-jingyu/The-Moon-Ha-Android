package com.innerpeace.themoonha.ui.fragment.live

import android.os.Bundle
import android.util.Log
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
import com.innerpeace.themoonha.adapter.live.LiveMyLiveLessonListAdapter
import com.innerpeace.themoonha.data.model.live.LiveLessonResponse
import com.innerpeace.themoonha.data.repository.LiveRepository
import com.innerpeace.themoonha.databinding.FragmentLiveMyLessonListBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewmodel.LiveViewModel
import com.innerpeace.themoonha.viewmodel.factory.LiveViewModelFactory

/**
 * 실시간 강좌 - 내 강좌 목록 조회 프래그먼트
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
class LiveMyLessonListFragment : Fragment() {
    private var _binding: FragmentLiveMyLessonListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LiveMyLiveLessonListAdapter
    private val viewModel: LiveViewModel by viewModels {
        LiveViewModelFactory(LiveRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLiveMyLessonListBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as? MainActivity)?.apply {
            setToolbarTitle("실시간 강좌")
            showToolbar()
            showBottomNavigation()
        }

        setHasOptionsMenu(true)
        setupToOnAir()
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
                        viewModel.getLiveLessonListWithMember()
                    }
                    1 -> {
                        viewModel.getLiveLessonListWithMemberOrderByTitle()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.getLiveLessonListWithMember()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.fieldListRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = LiveMyLiveLessonListAdapter(emptyList()) { content ->
            navigateToLiveLessonStreamingMain(content)
        }
        binding.fieldListRecyclerView.adapter = adapter
    }

    private fun navigateToLiveLessonStreamingMain(content: LiveLessonResponse) {
        viewModel.getLiveLessonDetail(content.liveId)

        viewModel.liveLessonDetailResponse.asLiveData().observe(viewLifecycleOwner) { response ->
            if (response == null) {
                Log.e("LiveMainFragment", "detailResponse is null")
                return@observe
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, LiveStreamingMainFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("liveLessonDetailResponse", response)
                    }
                })
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupToOnAir() {
        binding.onAir.setOnClickListener {
            findNavController().navigate(R.id.action_myLiveLesson_to_onAir)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
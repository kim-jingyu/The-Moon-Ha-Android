package com.innerpeace.themoonha.ui.fragment.lounge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.lounge.LoungeHomePostViewAdapter
import com.innerpeace.themoonha.viewModel.SharedViewModel
import com.innerpeace.themoonha.data.model.lounge.LoungePostListResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LoungeService
import com.innerpeace.themoonha.data.repository.LoungeRepository
import com.innerpeace.themoonha.databinding.FragmentLoungeHomeLoungeTabBinding
import com.innerpeace.themoonha.ui.util.ConditionalScrollLayoutManager
import com.innerpeace.themoonha.viewModel.LoungeViewModel
import com.innerpeace.themoonha.viewModel.factory.LoungeViewModelFactory

/**
 * 라운지 라운지 탭 프레그먼트
 * @author 조희정
 * @since 2024.09.01
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.01  	조희정       최초 생성
 * 2024.09.01   조희정       게시물 목록 recycler view 구현
 * </pre>
 */
class LoungeHomeLoungeTabFragment : Fragment() {
    private var _binding: FragmentLoungeHomeLoungeTabBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var layoutManager: ConditionalScrollLayoutManager

    private lateinit var adapter: LoungeHomePostViewAdapter
    private val viewModel: LoungeViewModel by activityViewModels {
        LoungeViewModelFactory(LoungeRepository(ApiClient.getClient().create(LoungeService::class.java)))
    }

    private var loungeId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoungeHomeLoungeTabBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // 스크롤 control
        layoutManager = ConditionalScrollLayoutManager(context)
        binding.rvPostList.layoutManager = layoutManager

        sharedViewModel.isScrollEnabled.observe(viewLifecycleOwner, Observer { isEnabled ->
            layoutManager.setScrollEnabled(isEnabled)
        })

        // 어댑터 초기화
        adapter = LoungeHomePostViewAdapter { loungeItem ->
            navigateToDetailFragment(loungeItem)
        }
        binding.rvPostList.adapter = adapter

        loungeId = viewModel.selectedLoungeId.value
        if (loungeId != null) {
            viewModel.resetPagination()
            viewModel.fetchLoungePostList(loungeId!!, 10)
        }

        // 데이터 변경
        viewModel.loungePostList.observe(viewLifecycleOwner, Observer { post ->
            if (post != null) {
                adapter.setItems(post)
            }
        })

        // 무한 스크롤
        binding.rvPostList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItemPosition + 1 >= totalItemCount && viewModel.hasMoreData()) {
                    loungeId?.let {
                        viewModel.fetchLoungePostList(it, 10)
                    }
                }
            }
        })
    }

    // 페이지 이동
    private fun navigateToDetailFragment(item: LoungePostListResponse) {
        viewModel.setSelectedLoungePostId(item.loungePostId)
        findNavController().navigate(R.id.action_loungeHomeFragment_to_loungePostFragment)
    }

    override fun onResume() {
        super.onResume()

        loungeId = viewModel.selectedLoungeId.value
        if (loungeId != null) {
            viewModel.resetPagination()
            adapter.setItems(emptyList())
            viewModel.fetchLoungePostList(loungeId!!, 10)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
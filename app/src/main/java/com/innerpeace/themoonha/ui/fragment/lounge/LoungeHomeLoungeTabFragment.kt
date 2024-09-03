package com.innerpeace.themoonha.ui.fragment.lounge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.lounge.LoungeHomePostViewAdapter
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LoungeService
import com.innerpeace.themoonha.data.repository.LoungeRepository
import com.innerpeace.themoonha.databinding.FragmentLoungeHomeLoungeTabBinding
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

    private lateinit var adapter: LoungeHomePostViewAdapter
    private val viewModel: LoungeViewModel by activityViewModels {
        LoungeViewModelFactory(LoungeRepository(ApiClient.getClient().create(LoungeService::class.java)))
    }

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

        // 데이터 불러오기
        viewModel.loungeHome.observe(viewLifecycleOwner, Observer { home ->
            if (home != null) {
                setupPostRecyclerView(home.loungePostList)
            }
        })
    }

    // 게시물 목록
    private fun setupPostRecyclerView(item: List<LoungeHomeResponse.LoungePost>) {
        adapter = LoungeHomePostViewAdapter { loungeItem ->
            navigateToDetailFragment(loungeItem)
        }
        binding.rvPostList.adapter = adapter

        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvPostList.layoutManager = linearLayoutManager

        adapter.setItems(item)
    }

    // 페이지 이동
    private fun navigateToDetailFragment(item: LoungeHomeResponse.LoungePost) {
        viewModel.setSelectedLoungePostId(item.loungePostId)
        findNavController().navigate(R.id.action_loungeHomeFragment_to_loungePostFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
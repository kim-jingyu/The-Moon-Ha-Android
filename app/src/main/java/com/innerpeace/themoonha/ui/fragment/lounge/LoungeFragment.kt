package com.innerpeace.themoonha.ui.fragment.lounge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.lounge.LoungeListViewAdapter
import com.innerpeace.themoonha.data.model.lounge.LoungeListResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LoungeService
import com.innerpeace.themoonha.data.repository.LoungeRepository
import com.innerpeace.themoonha.databinding.FragmentLoungeBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.LoungeViewModel
import com.innerpeace.themoonha.viewModel.factory.LoungeViewModelFactory

/**
 * 라운지 프래그먼트
 * @author 조희정
 * @since 2024.08.24
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.24  	조희정       최초 생성
 * 2024.08.30   조희정       라운지 목록 Recycler View 구현
 * </pre>
 */
class LoungeFragment : Fragment() {

    private var _binding: FragmentLoungeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LoungeListViewAdapter
    private val viewModel: LoungeViewModel by activityViewModels {
        LoungeViewModelFactory(LoungeRepository(ApiClient.getClient().create(LoungeService::class.java)))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoungeBinding.inflate(inflater, container, false)
        val view = binding.root

        // 툴바 제목 변경
        (activity as? MainActivity)?.showToolbar()
        (activity as? MainActivity)?.setToolbarTitle("라운지")
        (activity as? MainActivity)?.showNavigationBar()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 라운지 목록 보여주기
        setupRecyclerView()
    }

    // 라운지 목록 Recycler view
    private fun setupRecyclerView() {

        adapter = LoungeListViewAdapter { loungeItem ->
            navigateToDetailFragment(loungeItem)
        }
        binding.rvLoungeList.adapter = adapter

        viewModel.loungeList.observe(viewLifecycleOwner, Observer { lounges ->
            if (lounges != null) {
                adapter.setItems(lounges)
            }
        })

        viewModel.fetchLoungeList()
    }

    // 라운지 페이지로 이동
    private fun navigateToDetailFragment(item: LoungeListResponse) {
        viewModel.setSelectedLoungeId(item.loungeId)
        findNavController().navigate(R.id.action_fragment_lounge_to_loungeHomeFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
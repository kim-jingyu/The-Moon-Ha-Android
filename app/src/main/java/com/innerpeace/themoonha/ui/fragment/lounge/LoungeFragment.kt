package com.innerpeace.themoonha.ui.fragment.lounge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.innerpeace.themoonha.adapter.LoungeAdapter
import com.innerpeace.themoonha.databinding.FragmentLoungeBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewmodel.LoungeViewModel

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

    private lateinit var loungeAdaper: LoungeAdapter
    private val loungeViewModel: LoungeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoungeBinding.inflate(inflater, container, false)
        val view = binding.root

        // 툴바 제목 변경
        (activity as? MainActivity)?.setToolbarTitle("라운지")

        // 라운지 목록 보여주기
        setupRecyclerView()

        return view
    }

    // 라운지 목록 Recycler view
    private fun setupRecyclerView() {
        loungeAdaper = LoungeAdapter()
        binding.rvLoungeList.adapter = loungeAdaper

        // 데이터 받아오기
        val loungeList = loungeViewModel.fetchLoungeList()
        loungeAdaper.setItems(loungeList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
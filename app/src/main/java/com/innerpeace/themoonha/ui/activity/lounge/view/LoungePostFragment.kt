package com.innerpeace.themoonha.ui.activity.lounge.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.innerpeace.themoonha.databinding.FragmentLoungePostBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity

/**
 * 라운지 게시글 상세 프래그먼트
 * @author 조희정
 * @since 2024.08.23
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.24  	조희정       최초 생성
 * </pre>
 */
class LoungePostFragment : Fragment() {
    private var _binding: FragmentLoungePostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoungePostBinding.inflate(inflater, container, false)
        val view = binding.root

        // 툴바 제목 변경
        (activity as? MainActivity)?.setToolbarTitle("라운지 개별 이름")

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
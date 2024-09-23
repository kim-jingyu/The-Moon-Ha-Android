package com.innerpeace.themoonha.ui.fragment.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.innerpeace.themoonha.databinding.FragmentLiveStreamingChatBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity

/**
 * 실시간 강좌 스트리밍 채팅용 프래그먼트
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
class LiveStreamingChatFragment : Fragment() {
    private var _binding: FragmentLiveStreamingChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLiveStreamingChatBinding.inflate(inflater, container, false)
        val view = binding.root

        // 툴바 제목 변경
        (activity as? MainActivity)?.setToolbarTitle("실시간 강좌")

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
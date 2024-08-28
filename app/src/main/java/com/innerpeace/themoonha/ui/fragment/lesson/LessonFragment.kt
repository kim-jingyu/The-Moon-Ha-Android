package com.innerpeace.themoonha.ui.fragment.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.databinding.FragmentLessonBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity

class LessonFragment : Fragment() {
    private var _binding: FragmentLessonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLessonBinding.inflate(inflater, container, false)
        val view = binding.root

        // 툴바 제목 변경
        (activity as? MainActivity)?.setToolbarTitle("문화센터")

        // 버튼 이동 테스트
        binding.btnCraft.setOnClickListener{
            findNavController().navigate(R.id.action_fragment_lesson_to_craftFragment)
        }

        binding.btnMyInfo.setOnClickListener{
            findNavController().navigate(R.id.action_fragment_lesson_to_cartFragment)
        }

        binding.btnLive.setOnClickListener{
            findNavController().navigate(R.id.action_fragment_lesson_to_liveFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
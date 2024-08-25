package com.innerpeace.themoonha.ui.activity.lesson.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.innerpeace.themoonha.databinding.FragmentLessonDetailBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity

class LessonDetailFragment : Fragment() {
    private var _binding: FragmentLessonDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLessonDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        // 툴바 제목 변경
        (activity as? MainActivity)?.setToolbarTitle("강좌 상세")

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
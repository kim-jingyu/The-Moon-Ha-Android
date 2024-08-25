package com.innerpeace.themoonha.ui.activity.craft.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.innerpeace.themoonha.databinding.FragmentCraftBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity

class CraftFragment : Fragment() {
    private var _binding: FragmentCraftBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCraftBinding.inflate(inflater, container, false)
        val view = binding.root

        // 툴바 제목 변경
        (activity as? MainActivity)?.setToolbarTitle("문화공방")

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
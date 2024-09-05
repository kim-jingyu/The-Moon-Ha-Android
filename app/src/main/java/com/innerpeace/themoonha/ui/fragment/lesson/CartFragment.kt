package com.innerpeace.themoonha.ui.fragment.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.innerpeace.themoonha.adapter.CartAdapter
import com.innerpeace.themoonha.databinding.FragmentCartBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as? MainActivity)?.setToolbarTitle("나의 문화센터")

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = CartAdapter(this)
        viewPager.adapter = adapter

        viewPager.setCurrentItem(2, false)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "수강내역"
                1 -> "대기강좌"
                2 -> "장바구니"
                3 -> "수강자 관리"
                else -> null
            }
        }.attach()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

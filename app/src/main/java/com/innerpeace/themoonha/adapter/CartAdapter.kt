package com.innerpeace.themoonha.adapter

import CartContentFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.innerpeace.themoonha.ui.fragment.lesson.LessonFragment

class CartAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LessonFragment()
            1 -> LessonFragment()
            2 -> CartContentFragment()
            3 -> LessonFragment()
            else -> throw IllegalStateException("Unexpected position: $position")
        }
    }
}

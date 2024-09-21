package com.innerpeace.themoonha.adapter.lesson

import com.innerpeace.themoonha.ui.fragment.lesson.CartContentFragment
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.innerpeace.themoonha.ui.fragment.lesson.LessonFragment

/**
 * 장바구니 어댑터
 *
 * @author 손승완
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.05  	손승완       최초 생성
 * </pre>
 * @since 2024.09.05
 */
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

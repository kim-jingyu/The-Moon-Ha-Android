package com.innerpeace.themoonha.ui.util

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class ConditionalScrollLayoutManager(context: Context?) : LinearLayoutManager(context) {

    private var isScrollEnabled = false

    fun setScrollEnabled(flag: Boolean) {
        isScrollEnabled = flag
    }

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }
}
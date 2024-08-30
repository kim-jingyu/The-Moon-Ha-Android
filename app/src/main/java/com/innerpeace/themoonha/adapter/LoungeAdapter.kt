package com.innerpeace.themoonha.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.LoungeListResponse
import com.innerpeace.themoonha.ui.fragment.lounge.LoungeViewHolder

class LoungeAdapter : RecyclerView.Adapter<LoungeViewHolder>() {
    private val loungeList: ArrayList<LoungeListResponse> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoungeViewHolder {
        return LoungeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LoungeViewHolder, position: Int) {
        holder.onBind(loungeList[position])
    }

    override fun getItemCount(): Int = loungeList.size

    fun setItems(lounge: List<LoungeListResponse>) {
        loungeList.clear()
        loungeList.addAll(lounge)
        notifyDataSetChanged()
    }
}
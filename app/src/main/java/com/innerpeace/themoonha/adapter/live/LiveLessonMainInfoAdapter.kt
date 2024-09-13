package com.innerpeace.themoonha.adapter.live

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.live.LiveLessonInfo
import com.innerpeace.themoonha.databinding.FragmentLiveStreamingMainInfoItemBinding

class LiveLessonMainInfoAdapter(private val cardList: List<LiveLessonInfo>) : RecyclerView.Adapter<LiveLessonMainInfoAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: FragmentLiveStreamingMainInfoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentLiveStreamingMainInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardItem = cardList[position]
        holder.binding.title.text = cardItem.title
        holder.binding.content.text = cardItem.content
    }

    override fun getItemCount(): Int = cardList.size
}
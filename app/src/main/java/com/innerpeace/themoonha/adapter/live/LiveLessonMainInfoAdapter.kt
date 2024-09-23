package com.innerpeace.themoonha.adapter.live

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.live.LiveLessonInfo
import com.innerpeace.themoonha.databinding.FragmentLiveStreamingMainInfoItemBinding

/**
 * 실시간 강좌 스트리밍 정보용 어댑터
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
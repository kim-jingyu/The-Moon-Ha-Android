package com.innerpeace.themoonha.adapter.lounge

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
import com.innerpeace.themoonha.adapter.lounge.item.LoungeHomeNoticeViewHolder

/**
 * 라운지 홈 공지 Recycler View
 * @author 조희정
 * @since 2024.08.30
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.30  	조희정       최초 생성
 * </pre>
 */
class LoungeHomeNoticeViewAdapter(
    private val clickListener: (LoungeHomeResponse.LoungeNoticePost) -> Unit
) : RecyclerView.Adapter<LoungeHomeNoticeViewHolder>() {

    private val loungeNoticeList: ArrayList<LoungeHomeResponse.LoungeNoticePost> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoungeHomeNoticeViewHolder {
        return LoungeHomeNoticeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LoungeHomeNoticeViewHolder, position: Int) {
        holder.onBind(loungeNoticeList[position], clickListener)
    }

    override fun getItemCount(): Int = loungeNoticeList.size

    fun setItems(items: List<LoungeHomeResponse.LoungeNoticePost>) {
        loungeNoticeList.clear()
        loungeNoticeList.addAll(items)
        notifyDataSetChanged()
    }
}

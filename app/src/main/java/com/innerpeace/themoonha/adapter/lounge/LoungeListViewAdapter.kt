package com.innerpeace.themoonha.adapter.lounge

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.lounge.LoungeListResponse
import com.innerpeace.themoonha.adapter.lounge.viewHolder.LoungeListViewHolder

/**
 * 라운지 목록 Recycler View
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
class LoungeListViewAdapter(
    private val clickListener: (LoungeListResponse) -> Unit) : RecyclerView.Adapter<LoungeListViewHolder>() {
    private val loungeList: ArrayList<LoungeListResponse> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoungeListViewHolder {
        return LoungeListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LoungeListViewHolder, position: Int) {
        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams

        // 짝수 번째 아이템일 때 오른쪽에만 마진 15dp 설정
        if (position % 2 == 0) {
            layoutParams.setMargins(0, 0, 20, 0) // 오른쪽 마진 15
        } else {
            layoutParams.setMargins(20, 0, 0, 0) // 마진 없음
        }

        holder.itemView.layoutParams = layoutParams
        holder.onBind(loungeList[position], clickListener)
    }

    override fun getItemCount(): Int = loungeList.size

    fun setItems(items: List<LoungeListResponse>) {
        loungeList.clear()
        loungeList.addAll(items)
        notifyDataSetChanged()
    }
}
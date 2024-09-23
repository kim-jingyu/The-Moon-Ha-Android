package com.innerpeace.themoonha.adapter.lounge

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.adapter.lounge.viewHolder.LoungeHomePostViewHolder
import com.innerpeace.themoonha.data.model.lounge.LoungePostListResponse

/**
 * 라운지 게시물 Recycler View
 * @author 조희정
 * @since 2024.09.01
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.01  	조희정       최초 생성
 * </pre>
 */
class LoungeHomePostViewAdapter(
    private val clickListener: (LoungePostListResponse) -> Unit) : RecyclerView.Adapter<LoungeHomePostViewHolder>() {
    private val postList: ArrayList<LoungePostListResponse> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoungeHomePostViewHolder {
        return LoungeHomePostViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LoungeHomePostViewHolder, position: Int) {
        holder.onBind(postList[position], clickListener)
    }

    override fun getItemCount(): Int = postList.size

    fun setItems(newItems: List<LoungePostListResponse>) {
        postList.clear()
        postList.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<LoungePostListResponse>) {
        val previousSize = postList.size
        postList.addAll(newItems)
        notifyItemRangeInserted(previousSize, newItems.size)
    }
}
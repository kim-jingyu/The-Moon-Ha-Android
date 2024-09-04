package com.innerpeace.themoonha.adapter.lounge

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.adapter.lounge.item.LoungeHomePostViewHolder
import com.innerpeace.themoonha.data.model.lounge.LoungeListResponse
import com.innerpeace.themoonha.adapter.lounge.item.LoungeListViewHolder
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse

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
    private val clickListener: (LoungeHomeResponse.LoungePost) -> Unit) : RecyclerView.Adapter<LoungeHomePostViewHolder>() {
    private val postList: ArrayList<LoungeHomeResponse.LoungePost> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoungeHomePostViewHolder {
        return LoungeHomePostViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LoungeHomePostViewHolder, position: Int) {
        holder.onBind(postList[position], clickListener)
    }

    override fun getItemCount(): Int = postList.size

    fun setItems(items: List<LoungeHomeResponse.LoungePost>) {
        postList.clear()
        postList.addAll(items)
        notifyDataSetChanged()
    }
}
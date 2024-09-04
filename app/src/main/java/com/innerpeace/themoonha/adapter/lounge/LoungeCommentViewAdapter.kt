package com.innerpeace.themoonha.adapter.lounge

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.adapter.lounge.item.LoungeHomeCommentViewHolder
import com.innerpeace.themoonha.data.model.lounge.LoungePostResponse

/**
 * 라운지 게시물 댓글 Recycler View
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
class LoungeCommentViewAdapter : RecyclerView.Adapter<LoungeHomeCommentViewHolder>() {

    private val commentList: ArrayList<LoungePostResponse.LoungeComment> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoungeHomeCommentViewHolder {
        return LoungeHomeCommentViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LoungeHomeCommentViewHolder, position: Int) {
        holder.onBind(commentList[position])
    }

    override fun getItemCount(): Int = commentList.size

    fun setItems(items: List<LoungePostResponse.LoungeComment>) {
        commentList.clear()
        commentList.addAll(items)
        notifyDataSetChanged()
    }
}
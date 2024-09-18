package com.innerpeace.themoonha.adapter.lounge

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.adapter.lounge.viewHolder.LoungeHomePostViewHolder
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
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

    // 데이터를 덮어쓰는 메서드 (리셋할 때 사용)
    fun setItems(newItems: List<LoungePostListResponse>) {
        postList.clear()  // 기존 데이터 삭제
        postList.addAll(newItems)
        notifyDataSetChanged()  // 전체 데이터 갱신
    }

    // 데이터를 추가하는 메서드 (스크롤 시 새로운 데이터 추가)
    fun addItems(newItems: List<LoungePostListResponse>) {
        val previousSize = postList.size
        postList.addAll(newItems)
        notifyItemRangeInserted(previousSize, newItems.size)  // 추가된 데이터 범위만 갱신
    }
}
package com.innerpeace.themoonha.adapter.lounge

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.adapter.lounge.item.LoungeHomeMemberViewHolder
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse

/**
 * 라운지 홈 라운지 참여자 Recycler View
 * @author 조희정
 * @since 2024.08.31
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.31  	조희정       최초 생성
 * </pre>
 */
class LoungeHomeMemberViewAdapter(
    private val clickListener: (LoungeHomeResponse.LoungeMember) -> Unit) : RecyclerView.Adapter<LoungeHomeMemberViewHolder>() {
    private val memberList: ArrayList<LoungeHomeResponse.LoungeMember> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoungeHomeMemberViewHolder {
        return LoungeHomeMemberViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LoungeHomeMemberViewHolder, position: Int) {
        holder.onBind(memberList[position], clickListener)
    }

    override fun getItemCount(): Int = memberList.size

    fun setItems(items: List<LoungeHomeResponse.LoungeMember>) {
        memberList.clear()
        memberList.addAll(items)
        notifyDataSetChanged()
    }
}
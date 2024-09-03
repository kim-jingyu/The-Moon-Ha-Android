package com.innerpeace.themoonha.adapter.lounge.item

import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
import com.innerpeace.themoonha.data.model.lounge.LoungeListResponse
import com.innerpeace.themoonha.data.model.lounge.LoungePostResponse
import com.innerpeace.themoonha.databinding.ItemCommentBinding
import com.innerpeace.themoonha.databinding.ItemPostBinding

/**
 * 라운지 게시물 댓글 Recycler View Holder
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
class LoungeHomeCommentViewHolder(private val binding: ItemCommentBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(item: LoungePostResponse.LoungeComment) {
        // 프로필 이미지
        Glide.with(binding.ivProfileImage.context)
            .load(item.loungeMember.profileImgUrl)
            .circleCrop()
            .into(binding.ivProfileImage)

        // 텍스트
        binding.tvName.text = item.loungeMember.name
        binding.tvDate.text = item.createdAt
        binding.tvContent.text = item.content
    }

    companion object {
        fun from(parent: ViewGroup): LoungeHomeCommentViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCommentBinding.inflate(inflater, parent, false)
            return LoungeHomeCommentViewHolder(binding)
        }
    }
}
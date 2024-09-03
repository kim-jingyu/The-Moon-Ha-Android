package com.innerpeace.themoonha.adapter.lounge.item

/**
 * 라운지 홈 라운지 참여자 Recycler View Holder
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
import com.innerpeace.themoonha.data.model.lounge.LoungeListResponse
import com.innerpeace.themoonha.databinding.ItemLoungeBinding
import com.innerpeace.themoonha.databinding.ItemMemberBinding
import java.text.SimpleDateFormat
import java.util.*

class LoungeHomeMemberViewHolder(private val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root) {

    // Recycler View에 데이터 바인딩
    fun onBind(item: LoungeHomeResponse.LoungeMember, clickListener: (LoungeHomeResponse.LoungeMember) -> Unit) {
        Glide.with(binding.ivProfileImage.context)
            .load(item.profileImgUrl)
            .circleCrop()
            .into(binding.ivProfileImage)

        binding.tvName.text = item.name

        // 클릭 리스너 설정
        binding.root.setOnClickListener {
            clickListener(item)
        }
    }

    companion object {
        fun from(parent: ViewGroup): LoungeHomeMemberViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemMemberBinding.inflate(layoutInflater, parent, false)
            return LoungeHomeMemberViewHolder(binding)
        }
    }
}
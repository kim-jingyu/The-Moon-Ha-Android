package com.innerpeace.themoonha.adapter.lounge.viewHolder

/**
 * 라운지 홈 공지 Recycler View Holder
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
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.lounge.LoungeHomeResponse
import com.innerpeace.themoonha.databinding.ItemNoticeBinding

class LoungeHomeNoticeViewHolder(private val binding: ItemNoticeBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(item: LoungeHomeResponse.LoungeNoticePost, clickListener: (LoungeHomeResponse.LoungeNoticePost) -> Unit) {
        binding.tvContent.text = item.content

        // 클릭 리스너 설정
        binding.root.setOnClickListener {
            clickListener(item)
        }
    }

    companion object {
        fun from(parent: ViewGroup): LoungeHomeNoticeViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemNoticeBinding.inflate(inflater, parent, false)

            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            return LoungeHomeNoticeViewHolder(binding)
        }
    }
}
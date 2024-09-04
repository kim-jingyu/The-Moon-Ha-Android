package com.innerpeace.themoonha.adapter.lounge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.lounge.item.LoungeHomeCommentViewHolder
import com.innerpeace.themoonha.data.model.lounge.LoungePostResponse
import com.innerpeace.themoonha.databinding.ItemPostImageBinding

/**
 * 라운지 게시물 이미지 Recycler View
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
class LoungePostImageAdapter(
    private val imageUrls: List<String>
) : RecyclerView.Adapter<LoungePostImageAdapter.LoungePostImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoungePostImageViewHolder {
        return LoungePostImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LoungePostImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size

    class LoungePostImageViewHolder(private val binding: ItemPostImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            // Glide를 사용하여 이미지 로드
            Glide.with(binding.image.context)
                .load(imageUrl)
                .transform(FitCenter(), RoundedCorners(20))
                .into(binding.image)
        }

        companion object {
            fun from(parent: ViewGroup): LoungePostImageViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemPostImageBinding.inflate(inflater, parent, false)
                return LoungePostImageViewHolder(binding)
            }
        }
    }
}
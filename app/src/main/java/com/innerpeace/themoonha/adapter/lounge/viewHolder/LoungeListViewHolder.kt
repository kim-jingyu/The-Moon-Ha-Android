package com.innerpeace.themoonha.adapter.lounge.viewHolder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.innerpeace.themoonha.data.model.lounge.LoungeListResponse
import com.innerpeace.themoonha.databinding.ItemLoungeBinding

/**
 * 라운지 목록 Recycler View Holder
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
class LoungeListViewHolder(private val binding: ItemLoungeBinding) : RecyclerView.ViewHolder(binding.root) {

    // Recycler View에 데이터 바인딩
    fun onBind(item: LoungeListResponse, clickListener: (LoungeListResponse) -> Unit) {

        // 이미지
        Glide.with(binding.ivLoungeImage.context)
            .load(item.loungeImgUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(80)))
            .into(binding.ivLoungeImage)

        binding.tvLoungeTitle.text = item.title

        item.latestPostTime?.let { latestPostTime ->
            binding.ivNewIcon.visibility = if (latestPostTime.contains("전")) View.VISIBLE else View.GONE
            binding.tvLatestPostTime.text = latestPostTime
        }

        // 클릭 리스너 설정
        binding.root.setOnClickListener {
            clickListener(item)
        }
    }

    companion object {
        fun from(parent: ViewGroup): LoungeListViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemLoungeBinding.inflate(layoutInflater, parent, false)
            return LoungeListViewHolder(binding)
        }
    }
}
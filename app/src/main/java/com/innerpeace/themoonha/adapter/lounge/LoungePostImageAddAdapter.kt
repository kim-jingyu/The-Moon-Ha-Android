package com.innerpeace.themoonha.adapter.lounge

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.databinding.ItemImageAddBinding

/**
 * 라운지 게시물 작성 이미지 추가 Recycler View
 * @author 조희정
 * @since 2024.09.03
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.03  	조희정       최초 생성
 * </pre>
 */
class LoungePostImageAddAdapter(private val items: ArrayList<Uri>, val context: Context) :
    RecyclerView.Adapter<LoungePostImageAddAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = items[position]
        holder.onBind(item)
    }

    override fun getItemCount(): Int = items.size

    class ImageViewHolder(private val binding: ItemImageAddBinding) : RecyclerView.ViewHolder(binding.root) {

        private val imageView: ImageView = binding.image

        fun onBind(item: Uri) {
            // 이미지 로드
            Glide.with(binding.root.context).load(item)
                .override(100, 100)
                .into(imageView)
        }
    }
}
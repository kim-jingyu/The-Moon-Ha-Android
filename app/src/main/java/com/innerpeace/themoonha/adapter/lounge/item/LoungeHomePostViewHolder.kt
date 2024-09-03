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
import com.innerpeace.themoonha.databinding.ItemPostBinding

/**
 * 라운지 게시물 Recycler View Holder
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
class LoungeHomePostViewHolder(private val binding: ItemPostBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun onBind(item: LoungeHomeResponse.LoungePost, clickListener: (LoungeHomeResponse.LoungePost) -> Unit) {
        // 프로필 이미지
        Glide.with(binding.ivProfileImage.context)
            .load(item.loungeMember.profileImgUrl)
            .circleCrop()
            .into(binding.ivProfileImage)

        // 텍스트
        binding.tvName.text = item.loungeMember.name
        binding.tvDate.text = item.createdAt
        binding.tvContent.text = item.content

        // 이미지 리스트
        bindImages(item.loungePostImgList)

        // 클릭 이벤트
        binding.root.setOnClickListener {
            clickListener(item)
        }
    }

    private fun bindImages(images: List<String>) {
        val gridLayout = binding.glImages
        gridLayout.removeAllViews()

        // 이미지가 없을 때
        if (images.isEmpty()) {
            gridLayout.visibility = View.GONE
            return
        }

        // 이미지가 있을 때
        gridLayout.visibility = View.VISIBLE

        val context = gridLayout.context
        val displayImages = images.take(4)

        when (displayImages.size) {
            1 -> {
                gridLayout.rowCount = 1
                gridLayout.columnCount = 1
            }
            2 -> {
                gridLayout.rowCount = 1
                gridLayout.columnCount = 2
            }
            3 -> {
                gridLayout.rowCount = 2
                gridLayout.columnCount = 2
            }
            4 -> {
                gridLayout.rowCount = 2
                gridLayout.columnCount = 2
            }
        }

        // 이미지 추가
        displayImages.forEachIndexed { index, imageUrl ->
            val imageView = ImageView(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.WRAP_CONTENT
                    height = GridLayout.LayoutParams.WRAP_CONTENT

                    val topMargin = 8
                    val bottomMargin = 8
                    val leftMargin = if (index % 2 == 0) 0 else 8
                    val rightMargin = if (index % 2 == 0) 8 else 0

                    setMargins(leftMargin, topMargin, rightMargin, bottomMargin)

                    when (displayImages.size) {
                        1 -> {
                            rowSpec = GridLayout.spec(0, 2f)
                            columnSpec = GridLayout.spec(0, 2f)
                        }
                        2 -> {
                            rowSpec = GridLayout.spec(0, 1f)
                            columnSpec = GridLayout.spec(index, 1f)
                        }
                        3, 4 -> {
                            rowSpec = GridLayout.spec(index / 2, 1f)
                            columnSpec = GridLayout.spec(index % 2, 1f)
                        }
                    }
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            // 이미지 로드
            Glide.with(context)
                .load(imageUrl)
                .into(imageView)

            gridLayout.addView(imageView)
        }
    }

    companion object {
        fun from(parent: ViewGroup): LoungeHomePostViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPostBinding.inflate(inflater, parent, false)
            return LoungeHomePostViewHolder(binding)
        }
    }
}

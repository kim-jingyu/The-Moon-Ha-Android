package com.innerpeace.themoonha.adapter.lounge.item

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.innerpeace.themoonha.R
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

    private fun bindImages(imageUrls: List<String>?) {
        val gridLayout = binding.glImages
        gridLayout.removeAllViews() // 기존의 뷰를 모두 제거합니다.

        if (imageUrls.isNullOrEmpty()) {
            gridLayout.visibility = View.GONE // 이미지가 없을 경우 GridLayout을 숨깁니다.
            return
        } else {
            gridLayout.visibility = View.VISIBLE
        }

        val context = gridLayout.context
        val marginSize = 4.dpToPx()

        when (imageUrls.size) {
            1 -> {
                gridLayout.columnCount = 1
                gridLayout.rowCount = 1
                val imageView = createImageView(context, marginSize)
                Glide.with(context)
                    .load(imageUrls[0])
                    .transform(CenterCrop(), RoundedCorners(20))
                    .into(imageView)
                gridLayout.addView(imageView, createGridLayoutParams(0, 0, 1, 1, marginSize))
            }
            2 -> {
                gridLayout.columnCount = 2
                gridLayout.rowCount = 1
                for (i in 0 until 2) {
                    val imageView = createImageView(context, marginSize)
                    Glide.with(context)
                        .load(imageUrls[i])
                        .transform(CenterCrop(), RoundedCorners(20))
                        .into(imageView)
                    gridLayout.addView(imageView, createGridLayoutParams(i % 2, 0, 1, 1, marginSize))
                }
            }
            3 -> {
                gridLayout.columnCount = 2
                gridLayout.rowCount = 2
                for (i in 0 until 3) {
                    val imageView = createImageView(context, marginSize)
                    Glide.with(context)
                        .load(imageUrls[i])
                        .transform(CenterCrop(), RoundedCorners(20))
                        .into(imageView)
                    if (i == 0) {
                        gridLayout.addView(imageView, createGridLayoutParams(0, 0, 2, 1, marginSize)) // 첫 번째 이미지는 세로로 2칸
                    } else {
                        gridLayout.addView(imageView, createGridLayoutParams((i + 1) % 2, 1, 1, 1, marginSize)) // 나머지 두 이미지는 아래쪽에 각 한 칸씩
                    }
                }
            }
            else -> {
                gridLayout.columnCount = 2
                gridLayout.rowCount = 2
                for (i in 0 until 4) { // 최대 4개 이미지만 표시
                    val imageView = createImageView(context, marginSize)
                    Glide.with(context)
                        .load(imageUrls[i])
                        .transform(CenterCrop(), RoundedCorners(20))
                        .into(imageView)
                    gridLayout.addView(imageView, createGridLayoutParams(i % 2, i / 2, 1, 1, marginSize))
                }
            }
        }
    }

    private fun createImageView(context: android.content.Context, marginSize: Int): ImageView {
        return ImageView(context).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(marginSize, marginSize, marginSize, marginSize)
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }

    private fun createGridLayoutParams(column: Int, row: Int, colSpan: Int, rowSpan: Int, marginSize: Int): GridLayout.LayoutParams {
        return GridLayout.LayoutParams().apply {
            this.columnSpec = GridLayout.spec(column, colSpan, 1f)
            this.rowSpec = GridLayout.spec(row, rowSpan, 1f)
            this.width = 0
            this.height = 0
            setMargins(marginSize, marginSize, marginSize, marginSize)
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    companion object {
        fun from(parent: ViewGroup): LoungeHomePostViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPostBinding.inflate(inflater, parent, false)
            return LoungeHomePostViewHolder(binding)
        }
    }
}

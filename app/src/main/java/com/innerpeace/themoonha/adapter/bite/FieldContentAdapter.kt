package com.innerpeace.themoonha.adapter.bite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.innerpeace.themoonha.data.model.field.FieldListResponse
import com.innerpeace.themoonha.databinding.FragmentFieldContentBinding
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class FieldContentAdapter(
    private val fieldList: List<FieldListResponse>,
    private val itemClickListener: (FieldListResponse) -> Unit
    ) : RecyclerView.Adapter<FieldContentAdapter.FieldContentViewHolder>() {
    inner class FieldContentViewHolder(val binding: FragmentFieldContentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FieldContentViewHolder {
        val binding = FragmentFieldContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FieldContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FieldContentViewHolder, position: Int) {
        val fieldItem = fieldList[position % fieldList.size]

        setContentImage(holder, fieldItem)
        setBottomContent(holder, fieldItem)

        holder.binding.lessonTitle.text = fieldItem.lessonTitle

        holder.binding.root.setOnClickListener {
            itemClickListener(fieldItem)
        }
    }

    private fun setBottomContent(
        holder: FieldContentViewHolder,
        fieldItem: FieldListResponse
    ) {
        holder.binding.title.text = fieldItem.title
        holder.binding.memberName.text = fieldItem.memberName

        Glide.with(holder.itemView.context)
            .load(fieldItem.profileImgUrl)
            .circleCrop()
            .into(holder.binding.profileImage)
    }

    private fun setContentImage(
        holder: FieldContentViewHolder,
        fieldItem: FieldListResponse
    ) {
        Glide.with(holder.itemView.context)
            .load(fieldItem.thumbnailUrl)
            .apply(
                RequestOptions.bitmapTransform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCornersTransformation(
                            10,
                            0,
                            RoundedCornersTransformation.CornerType.ALL
                        )
                    )
                )
            )
            .into(holder.binding.content)
    }

    override fun getItemCount(): Int = fieldList.size
}

package com.innerpeace.themoonha.adapter.bite

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterListResponse
import com.innerpeace.themoonha.databinding.FragmentBeforeAfterContentBinding
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * Before&After 어댑터
 * @author 김진규
 * @since 2024.08.25
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.25  	김진규       최초 생성
 * </pre>
 */
class BeforeAfterAdapter(
    private var contents: List<BeforeAfterListResponse>,
    private val itemClickListener: (Int) -> Unit)
    : RecyclerView.Adapter<BeforeAfterAdapter.ViewHolder>()
{

    class ViewHolder(val binding: FragmentBeforeAfterContentBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentBeforeAfterContentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val content = contents[position]
        listenEvent(holder, position)
        setupBeforeContent(holder, content)
        setupAfterContent(holder, content)
        setupBottomContent(holder, content)
    }

    private fun listenEvent(
        holder: ViewHolder,
        position: Int
    ) {
        holder.binding.root.setOnClickListener {
            itemClickListener(position)
        }
    }

    private fun setupBottomContent(
        holder: ViewHolder,
        content: BeforeAfterListResponse
    ) {
        holder.binding.title.text = content.title
        Glide.with(holder.binding.root.context)
            .load(content.profileImgUrl)
            .circleCrop()
            .into(holder.binding.profileImage)
        holder.binding.memberName.text = content.memberName
    }

    private fun setupBeforeContent(
        holder: ViewHolder,
        content: BeforeAfterListResponse
    ) {
        Glide.with(holder.binding.root.context)
            .load(content.beforeThumbnailUrl)
            .apply(
                RequestOptions.bitmapTransform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCornersTransformation(
                            20,
                            0,
                            RoundedCornersTransformation.CornerType.TOP
                        )
                    )
                )
            )
            .into(holder.binding.beforeImage)
    }

    private fun setupAfterContent(
        holder: ViewHolder,
        content: BeforeAfterListResponse
    ){
        Glide.with(holder.binding.root.context)
            .load(content.afterThumbnailUrl)
            .apply(
                RequestOptions.bitmapTransform(
                    MultiTransformation(
                        CenterCrop(),
                        RoundedCornersTransformation(
                            20,
                            0,
                            RoundedCornersTransformation.CornerType.BOTTOM
                        )
                    )
                )
            )
            .into(holder.binding.afterImage)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateContents(newContents: List<BeforeAfterListResponse>) {
        contents = newContents
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = contents.size
}
package com.innerpeace.themoonha.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.data.model.field.FieldListResponse
import com.innerpeace.themoonha.databinding.FragmentFieldContentBinding

class FieldCategoryAdapter(private val fieldItems: List<FieldListResponse>) : RecyclerView.Adapter<FieldCategoryAdapter.FieldCategoryViewHolder>() {
    inner class FieldCategoryViewHolder(val binding: FragmentFieldContentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldCategoryViewHolder {
        val binding =
            FragmentFieldContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FieldCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FieldCategoryViewHolder, position: Int) {
        val fieldItem = fieldItems[position]

        Glide.with(holder.binding.content.context)
            .load(fieldItem.thumbnailUrl)
            .into(holder.binding.content)

        holder.binding.title.text = fieldItem.title
        holder.binding.memberName.text = fieldItem.memberName

        Glide.with(holder.binding.profileImage.context)
            .load(fieldItem.profileImgUrl)
            .into(holder.binding.profileImage)
    }

    override fun getItemCount(): Int = fieldItems.size
}
package com.innerpeace.themoonha.adapter.bite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.field.FieldCategoryGroup
import com.innerpeace.themoonha.data.model.field.FieldListResponse
import com.innerpeace.themoonha.databinding.FragmentFieldCategoryBinding

class FieldListAdapter(
    private var fieldLists: List<FieldCategoryGroup>,
    private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<FieldListAdapter.FieldCategoryViewHolder>() {
    inner class FieldCategoryViewHolder(val binding: FragmentFieldCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldCategoryViewHolder {
        val binding = FragmentFieldCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FieldCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FieldCategoryViewHolder, position: Int) {
        val categoryGroup = fieldLists[position]

        holder.binding.categoryName.text = categoryGroup.category

        val fieldContentAdapter = FieldContentAdapter(categoryGroup.fieldList) { selectedPosition ->
            onItemClick(selectedPosition)
        }
        holder.binding.horizontalRecyclerView.apply {
            layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = fieldContentAdapter
            setHasFixedSize(true)
        }
    }

    fun update(newFieldLists: List<FieldCategoryGroup>) {
        this.fieldLists = newFieldLists
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = fieldLists.size
}
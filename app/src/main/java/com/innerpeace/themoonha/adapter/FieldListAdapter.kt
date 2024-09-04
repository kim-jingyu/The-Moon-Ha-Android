package com.innerpeace.themoonha.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.field.FieldCategoryGroup
import com.innerpeace.themoonha.databinding.FragmentFieldCategoryBinding

class FieldListAdapter(private var fieldLists: List<FieldCategoryGroup>) : RecyclerView.Adapter<FieldListAdapter.FieldCategoryViewHolder>() {
    inner class FieldCategoryViewHolder(val binding: FragmentFieldCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldCategoryViewHolder {
        val binding = FragmentFieldCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FieldCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FieldCategoryViewHolder, position: Int) {
        val categoryGroup = fieldLists[position]

        holder.binding.categoryName.text = categoryGroup.categoryName

        val fieldListAdapter = FieldCategoryAdapter(categoryGroup.fieldList)
        holder.binding.horizontalRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = fieldListAdapter
            setHasFixedSize(true)
        }
    }

    fun update(newFieldLists: List<FieldCategoryGroup>) {
        this.fieldLists = newFieldLists
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = fieldLists.size
}
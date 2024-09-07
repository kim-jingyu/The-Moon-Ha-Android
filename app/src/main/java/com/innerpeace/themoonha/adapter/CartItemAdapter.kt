package com.innerpeace.themoonha.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.lesson.CartResponse
import com.innerpeace.themoonha.data.model.lesson.TargetType
import com.innerpeace.themoonha.databinding.FragmentCartItemBinding
import com.innerpeace.themoonha.databinding.FragmentCartBranchBinding

class CartItemAdapter(private val onCheckedChange: (CartResponse, Boolean) -> Unit) :
    ListAdapter<CartItemAdapter.ListItem, RecyclerView.ViewHolder>(CartDiffCallback()) {

    private val itemColors = mutableMapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SECTION_HEADER) {
            val binding = FragmentCartBranchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SectionHeaderViewHolder(binding)
        } else {
            val binding = FragmentCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CartViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listItem = getItem(position)
        when (holder) {
            is SectionHeaderViewHolder -> holder.bind(listItem as ListItem.SectionHeader)
            is CartViewHolder -> holder.bind((listItem as ListItem.CartItem).cartItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.SectionHeader -> VIEW_TYPE_SECTION_HEADER
            is ListItem.CartItem -> VIEW_TYPE_CART_ITEM
        }
    }

    fun updateItemColors(eventColors: Map<String, Int>) {
        itemColors.clear()
        itemColors.putAll(eventColors)
        notifyDataSetChanged()
    }

    fun uncheckItem(cartItem: CartResponse) {
        val position = currentList.indexOfFirst {
            it is ListItem.CartItem && it.cartItem.cartId == cartItem.cartId
        }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    inner class CartViewHolder(private val binding: FragmentCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartResponse) {
            binding.cartItemTitle.text = cartItem.lessonTitle
            binding.period.text = cartItem.period.replace("-", ".")
            binding.lessonTime.text = "| " + cartItem.lessonTime.replace("-", ".")
            binding.tutorName.text = cartItem.tutorName
            if (cartItem.onlineYn) {
                binding.cost.text = "| " + cartItem.onlineCost.toString() + "원"
                binding.onlineYn.text = "온라인"
            } else {
                binding.cost.text = "| " + cartItem.cost.toString() + "원"
                binding.onlineYn.text = "오프라인"
            }

            binding.targetDescription.text = TargetType.fromId(cartItem.target.toInt())!!.description

            val color = itemColors[cartItem.cartId] ?: Color.TRANSPARENT
            binding.colorIndicator.setBackgroundColor(color)
            binding.cartItemCheckBox.setOnCheckedChangeListener(null)
            binding.cartItemCheckBox.isChecked = color != Color.TRANSPARENT
            binding.cartItemCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(cartItem, isChecked)
            }
        }
    }

    inner class SectionHeaderViewHolder(private val binding: FragmentCartBranchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(sectionHeader: ListItem.SectionHeader) {
            binding.branchName.text = sectionHeader.branchName
        }
    }

    sealed class ListItem {
        data class SectionHeader(val branchName: String) : ListItem()
        data class CartItem(val cartItem: CartResponse) : ListItem()
    }

    companion object {
        const val VIEW_TYPE_SECTION_HEADER = 0
        const val VIEW_TYPE_CART_ITEM = 1
    }
}

class CartDiffCallback : DiffUtil.ItemCallback<CartItemAdapter.ListItem>() {
    override fun areItemsTheSame(oldItem: CartItemAdapter.ListItem, newItem: CartItemAdapter.ListItem): Boolean {
        return if (oldItem is CartItemAdapter.ListItem.CartItem && newItem is CartItemAdapter.ListItem.CartItem) {
            oldItem.cartItem.cartId == newItem.cartItem.cartId
        } else if (oldItem is CartItemAdapter.ListItem.SectionHeader && newItem is CartItemAdapter.ListItem.SectionHeader) {
            oldItem.branchName == newItem.branchName
        } else {
            false
        }
    }

    override fun areContentsTheSame(oldItem: CartItemAdapter.ListItem, newItem: CartItemAdapter.ListItem): Boolean {
        return oldItem == newItem
    }
}

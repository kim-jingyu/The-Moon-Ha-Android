package com.innerpeace.themoonha.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.lesson.CartResponse
import com.innerpeace.themoonha.databinding.FragmentCartItemBinding

class CartItemAdapter(private val onCheckedChange: (CartResponse, Boolean) -> Unit) :
    ListAdapter<CartResponse, CartItemAdapter.CartViewHolder>(CartDiffCallback()) {

    private val itemColors = mutableMapOf<String, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = FragmentCartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = getItem(position)
        holder.bind(cartItem)
    }

    fun updateItemColors(eventColors: Map<String, Int>) {
        itemColors.clear()
        itemColors.putAll(eventColors)
        notifyDataSetChanged()
    }

    inner class CartViewHolder(private val binding: FragmentCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartResponse) {
            binding.cartItemTitle.text = cartItem.lessonTitle
            binding.period.text = cartItem.period.replace("-", ".")
            binding.lessonTime.text = "| " + cartItem.period.replace("-", ".")
            binding.tutorName.text = cartItem.tutorName
            binding.cost.text = "| " + cartItem.cost.toString() + "원"

            // 색상 설정
            val color = itemColors[cartItem.cartId] ?: Color.TRANSPARENT
            binding.colorIndicator.setBackgroundColor(color)

            // 체크박스 상태 설정
            binding.cartItemCheckBox.setOnCheckedChangeListener(null) // 이전 리스너 제거
            binding.cartItemCheckBox.isChecked = color != Color.TRANSPARENT
            binding.cartItemCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(cartItem, isChecked)
            }
        }
    }
}

class CartDiffCallback : DiffUtil.ItemCallback<CartResponse>() {
    override fun areItemsTheSame(oldItem: CartResponse, newItem: CartResponse): Boolean {
        return oldItem.cartId == newItem.cartId
    }

    override fun areContentsTheSame(oldItem: CartResponse, newItem: CartResponse): Boolean {
        return oldItem == newItem
    }
}
package com.innerpeace.themoonha.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.innerpeace.themoonha.data.model.craft.PrologueDTO
import com.innerpeace.themoonha.data.model.craft.PrologueThemeDTO
import com.innerpeace.themoonha.databinding.FragmentPrologueThemeBinding
import com.innerpeace.themoonha.viewModel.CraftViewModel

class PrologueThemeAdapter(
    private val themeList: List<PrologueThemeDTO>,
    private val onPrologueClick: (PrologueDTO) -> Unit
) : RecyclerView.Adapter<PrologueThemeAdapter.PrologueThemeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrologueThemeViewHolder {
        val binding =
            FragmentPrologueThemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrologueThemeViewHolder(binding, onPrologueClick)
    }

    override fun onBindViewHolder(holder: PrologueThemeViewHolder, position: Int) {
        val theme = themeList[position]
        holder.bind(theme)
    }

    override fun getItemCount(): Int = themeList.size

    class PrologueThemeViewHolder(
        private val binding: FragmentPrologueThemeBinding,
        private val onPrologueClick: (PrologueDTO) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val prologueAdapter = PrologueAdapter(emptyList(), onPrologueClick)

        init {
            binding.prologueRecyclerView.layoutManager =
                GridLayoutManager(binding.root.context, 2, GridLayoutManager.VERTICAL, false)
            binding.prologueRecyclerView.adapter = prologueAdapter
        }

        fun bind(theme: PrologueThemeDTO) {
            binding.themeNameTextView.text = theme.themeName
            binding.themeDescriptionTextView.text = theme.themeDescription
            prologueAdapter.updatePrologueList(theme.prologueList)
        }
    }
}

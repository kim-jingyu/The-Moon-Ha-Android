package com.innerpeace.themoonha.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterListResponse
import com.innerpeace.themoonha.databinding.FragmentBeforeAfterContentBinding
import com.innerpeace.themoonha.ui.fragment.beforeafter.BeforeAfterDetailFragment
import com.innerpeace.themoonha.viewmodel.BeforeAfterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
class BeforeAfterAdapter(private var contents: List<BeforeAfterListResponse>, private val fragment: Fragment) : RecyclerView.Adapter<BeforeAfterAdapter.ViewHolder>() {

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

        fragment.lifecycleScope.launch {
            setupBeforeContent(holder, content)
            setupAfterContent(holder, content)
            setupTextContent(holder, content)
            listenEvent(holder, content)
        }
    }

    private fun listenEvent(
        holder: ViewHolder,
        content: BeforeAfterListResponse
    ) {
        holder.binding.root.setOnClickListener {
            navigateToDetail(holder.binding.root.context, content)
        }

        holder.binding.beforeImage.setOnClickListener {
            navigateToDetail(holder.binding.root.context, content)
        }

        holder.binding.afterImage.setOnClickListener {
            navigateToDetail(holder.binding.root.context, content)
        }

        holder.binding.title.setOnClickListener {
            navigateToDetail(holder.binding.root.context, content)
        }
    }

    private suspend fun setupTextContent(
        holder: ViewHolder,
        content: BeforeAfterListResponse
    ) = withContext(Dispatchers.Main) {
        holder.binding.title.text = content.title
        Glide.with(holder.binding.root.context)
            .load(content.profileImgUrl)
            .circleCrop()
            .into(holder.binding.profileImage)
        holder.binding.memberName.text = content.memberName
    }

    private suspend fun setupBeforeContent(
        holder: ViewHolder,
        content: BeforeAfterListResponse
    ) = withContext(Dispatchers.Main) {
        Glide.with(holder.binding.root.context)
            .load(content.beforeThumbnailUrl)
            .into(holder.binding.beforeImage)
    }

    private suspend fun setupAfterContent(
        holder: ViewHolder,
        content: BeforeAfterListResponse
    ) = withContext(Dispatchers.Main) {
        Glide.with(holder.binding.root.context)
            .load(content.afterThumbnailUrl)
            .into(holder.binding.afterImage)
    }

    private fun navigateToDetail(context: Context, content: BeforeAfterListResponse) {
        val viewModel = ViewModelProvider(fragment).get(BeforeAfterViewModel::class.java)

        viewModel.getBeforeAfterDetail(content.beforeAfterId)
        viewModel.beforeAfterDetailResponse.asLiveData().observe(fragment.viewLifecycleOwner) { resp ->
            resp?.let {
                (context as? AppCompatActivity)?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.fragmentContainerView, BeforeAfterDetailFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable("beforeAfterContent", it)
                        }
                    })
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }
    }

    fun updateContents(newContents: List<BeforeAfterListResponse>) {
        contents = newContents
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = contents.size
}
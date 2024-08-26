package com.innerpeace.themoonha.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.innerpeace.themoonha.data.model.BeforeAfterContent
import com.innerpeace.themoonha.databinding.FragmentBeforeAfterContentBinding

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
class BeforeAfterAdapter(private val contents: List<BeforeAfterContent>) : RecyclerView.Adapter<BeforeAfterAdapter.ViewHolder>() {

    class ViewHolder(val binding: FragmentBeforeAfterContentBinding) : RecyclerView.ViewHolder(binding.root) {
        var beforePlayer: ExoPlayer? = null
        var afterPlayer: ExoPlayer? = null

        fun playVideo() {
            beforePlayer?.playWhenReady = true
            afterPlayer?.playWhenReady = true
        }

        fun stopVideo() {
            beforePlayer?.playWhenReady = false
            afterPlayer?.playWhenReady = false
        }
    }

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

        holder.binding.beforeVideo.useController = false
        holder.binding.afterVideo.useController = false

        val afterImageParams = holder.binding.afterImage.layoutParams as ConstraintLayout.LayoutParams
        val afterVideoParams = holder.binding.afterVideo.layoutParams as ConstraintLayout.LayoutParams

        if (content.beforeIsImage) {
            holder.binding.beforeImage.visibility = View.VISIBLE
            holder.binding.beforeVideo.visibility = View.GONE
            Glide.with(holder.binding.root.context)
                .load(content.beforeUrl)
                .into(holder.binding.beforeImage)

            afterImageParams.topToBottom = holder.binding.beforeImage.id
            afterVideoParams.topToBottom = holder.binding.beforeImage.id
        } else {
            holder.binding.beforeImage.visibility = View.GONE
            holder.binding.beforeVideo.visibility = View.VISIBLE

            holder.beforePlayer = ExoPlayer.Builder(holder.binding.root.context).build().apply {
                setMediaItem(MediaItem.fromUri(content.beforeUrl))
                prepare()
                playWhenReady = false
                repeatMode = ExoPlayer.REPEAT_MODE_ONE
            }
            holder.binding.beforeVideo.player = holder.beforePlayer

            afterImageParams.topToBottom = holder.binding.beforeVideo.id
            afterVideoParams.topToBottom = holder.binding.beforeVideo.id
        }

        holder.binding.afterImage.layoutParams = afterImageParams
        holder.binding.afterVideo.layoutParams = afterVideoParams

        val titleParams = holder.binding.title.layoutParams as ConstraintLayout.LayoutParams

        if (content.afterIsImage) {
            holder.binding.afterImage.visibility = View.VISIBLE
            holder.binding.afterVideo.visibility = View.GONE
            Glide.with(holder.binding.root.context)
                .load(content.afterUrl)
                .into(holder.binding.afterImage)

            titleParams.topToBottom = holder.binding.afterImage.id
        } else {
            holder.binding.afterImage.visibility = View.GONE
            holder.binding.afterVideo.visibility = View.VISIBLE

            holder.afterPlayer = ExoPlayer.Builder(holder.binding.root.context).build().apply {
                setMediaItem(MediaItem.fromUri(content.afterUrl))
                prepare()
                playWhenReady = false
                repeatMode = ExoPlayer.REPEAT_MODE_ONE
            }
            holder.binding.afterVideo.player = holder.afterPlayer

            titleParams.topToBottom = holder.binding.afterVideo.id
        }

        holder.binding.title.layoutParams = titleParams

        holder.binding.title.text = content.title
        Glide.with(holder.binding.root.context)
            .load(content.profileImageUrl)
            .into(holder.binding.profileImage)
        holder.binding.memberName.text = content.memberName
    }

    override fun getItemCount(): Int = contents.size
}
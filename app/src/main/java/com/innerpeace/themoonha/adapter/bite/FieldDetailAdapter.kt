package com.innerpeace.themoonha.adapter.bite

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.innerpeace.themoonha.data.model.field.FieldDetailResponse
import com.innerpeace.themoonha.databinding.FragmentFieldDetailItemBinding

class FieldDetailAdapter(private val contents: List<FieldDetailResponse>) : RecyclerView.Adapter<FieldDetailAdapter.FieldDetailViewHolder>() {
    inner class FieldDetailViewHolder(private val binding: FragmentFieldDetailItemBinding): RecyclerView.ViewHolder(binding.root) {
        private var player: ExoPlayer? = null
        private var isTextExpanded = false

        fun bind(content: FieldDetailResponse) {
            setupContent(content)
            setupTextContent(content)
            setHashtags(content.hashtags)
        }

        private fun setupContent(content: FieldDetailResponse) {
            if (content.contentIsImage == 1) {
                binding.imageDetail.visibility = View.VISIBLE
                binding.videoDetail.visibility = View.GONE

                Glide.with(binding.root.context)
                    .load(content.contentUrl)
                    .into(binding.imageDetail)
            } else {
                binding.imageDetail.visibility = View.GONE
                binding.videoDetail.visibility = View.VISIBLE
                player = ExoPlayer.Builder(binding.root.context).build().apply {
                    setMediaItem(MediaItem.fromUri(content.contentUrl))
                    prepare()
                    playWhenReady = true
                    repeatMode = Player.REPEAT_MODE_ALL
                }
                binding.videoDetail.player = player
                binding.videoDetail.useController = false
                controlVideoPlayer(player)
            }
        }

        private fun setupTextContent(content: FieldDetailResponse) {
            binding.titleDetail.text = content.title
            binding.memberNameDetail.text = content.memberName

            Glide.with(binding.root.context)
                .load(content.profileImgUrl)
                .circleCrop()
                .into(binding.profileImageDetail)

            binding.titleDetail.post {
                if (binding.titleDetail.layout.getEllipsisCount(0) > 0) {
                    binding.moreButton.visibility = View.VISIBLE
                } else {
                    binding.moreButton.visibility = View.GONE
                }
            }

            binding.moreButton.setOnClickListener {
                if (!isTextExpanded) {
                    binding.titleDetail.maxLines = Int.MAX_VALUE
                    binding.titleDetail.ellipsize = null
                    binding.moreButton.visibility = View.GONE
                    isTextExpanded = !isTextExpanded
                    binding.profileImageDetail.visibility = View.GONE
                    binding.memberNameDetail.visibility = View.GONE
                }
            }

            binding.titleDetail.setOnClickListener {
                if (isTextExpanded) {
                    binding.titleDetail.maxLines = 1
                    binding.titleDetail.ellipsize = TextUtils.TruncateAt.END
                    binding.moreButton.text = "더보기"
                    binding.moreButton.visibility = View.VISIBLE
                    isTextExpanded = false
                    binding.profileImageDetail.visibility = View.VISIBLE
                    binding.memberNameDetail.visibility = View.VISIBLE
                }
            }
        }

        private fun controlVideoPlayer(
            player: ExoPlayer?
        ) {
            player?.let {
                binding.videoDetail.setOnClickListener {
                    if (player.isPlaying) {
                        player.pause()
                        binding.pauseIcon.visibility = View.VISIBLE
                        binding.playIcon.visibility = View.GONE
                    } else {
                        player.play()
                        binding.playIcon.visibility = View.VISIBLE
                        binding.pauseIcon.visibility = View.GONE
                    }
                }
            }
        }


        private fun setHashtags(hashtags: List<String>) {
            val flow = binding.hashtagFlow
            val idList = mutableListOf<Int>()

            if (hashtags.isNullOrEmpty()) {
                flow.referencedIds = intArrayOf()
                return
            }

            for (hashtag in hashtags) {
                val textView = TextView(binding.root.context).apply {
                    id = View.generateViewId()
                    text = "#$hashtag"
                    setTextColor(ContextCompat.getColor(binding.root.context, android.R.color.white))
                    setPadding(0, 4, 8, 4)
                    textSize = 12f
                }
                binding.root.addView(textView)
                idList.add(textView.id)
            }
            flow.referencedIds = idList.toIntArray()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldDetailAdapter.FieldDetailViewHolder {
        val binding = FragmentFieldDetailItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FieldDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FieldDetailViewHolder, position: Int) {
        holder.bind(contents[position])
    }

    override fun getItemCount(): Int = contents.size
}
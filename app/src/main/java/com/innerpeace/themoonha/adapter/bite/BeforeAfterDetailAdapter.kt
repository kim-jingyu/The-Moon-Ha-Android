package com.innerpeace.themoonha.adapter.bite

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterDetailResponse
import com.innerpeace.themoonha.databinding.FragmentBeforeAfterDetailItemBinding

class BeforeAfterDetailAdapter(private val contents: List<BeforeAfterDetailResponse>) : RecyclerView.Adapter<BeforeAfterDetailAdapter.BeforeAfterDetailViewHolder>() {
    inner class BeforeAfterDetailViewHolder(private val binding: FragmentBeforeAfterDetailItemBinding): RecyclerView.ViewHolder(binding.root) {
        private var beforePlayer: ExoPlayer? = null
        private var afterPlayer: ExoPlayer? = null
        private var isTextExpanded = false

        fun bind(content: BeforeAfterDetailResponse) {
            setupBeforeContent(content)
            setupAfterContent(content)
            setupTextContent(content)
            setHashtags(content.hashtags)
        }

        private fun setupBeforeContent(content: BeforeAfterDetailResponse) {
            val beforeImageParams = binding.beforeImageDetail.layoutParams as ConstraintLayout.LayoutParams
            val beforeVideoParams = binding.beforeVideoDetail.layoutParams as ConstraintLayout.LayoutParams

            if (content.beforeIsImage == 1 && content.beforeUrl.isNullOrEmpty().not()) {
                binding.beforeImageDetail.visibility = View.VISIBLE
                binding.beforeVideoDetail.visibility = View.GONE

                Glide.with(binding.root.context)
                    .load(content.beforeUrl)
                    .error(R.drawable.ic_play)
                    .into(binding.beforeImageDetail)

                beforeImageParams.height = 0
                beforeImageParams.matchConstraintPercentHeight = 0.5f
            } else {
                binding.beforeImageDetail.visibility = View.GONE
                binding.beforeVideoDetail.visibility = View.VISIBLE
                beforePlayer = ExoPlayer.Builder(binding.root.context).build().apply {
                    setMediaItem(MediaItem.fromUri(content.beforeUrl))
                    prepare()
                    playWhenReady = true
                }
                binding.beforeVideoDetail.player = beforePlayer
                binding.beforeVideoDetail.useController = false

                beforeVideoParams.height = 0
                beforeVideoParams.matchConstraintPercentHeight = 0.5f
            }
        }

        private fun setupAfterContent(content: BeforeAfterDetailResponse) {
            val afterImageParams = binding.afterImageDetail.layoutParams as ConstraintLayout.LayoutParams
            val afterVideoParams = binding.afterVideoDetail.layoutParams as ConstraintLayout.LayoutParams

            if (content.afterIsImage == 1) {
                binding.afterImageDetail.visibility = View.VISIBLE
                binding.afterVideoDetail.visibility = View.GONE
                Glide.with(binding.root.context)
                    .load(content.afterUrl)
                    .error(R.drawable.ic_play)
                    .into(binding.afterImageDetail)

                afterImageParams.height = 0
                afterImageParams.matchConstraintPercentHeight = 0.5f
            } else {
                binding.afterImageDetail.visibility = View.GONE
                binding.afterVideoDetail.visibility = View.VISIBLE
                afterPlayer = ExoPlayer.Builder(binding.root.context).build().apply {
                    setMediaItem(MediaItem.fromUri(content.afterUrl))
                    prepare()
                    playWhenReady = true
                }
                binding.afterVideoDetail.player = afterPlayer
                binding.afterVideoDetail.useController = false

                afterVideoParams.height = 0
                afterVideoParams.matchConstraintPercentHeight = 0.5f
            }
        }

        private fun setupTextContent(content: BeforeAfterDetailResponse) {
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
                }
            }

            binding.titleDetail.setOnClickListener {
                if (isTextExpanded) {
                    binding.titleDetail.maxLines = 1
                    binding.titleDetail.ellipsize = TextUtils.TruncateAt.END
                    binding.moreButton.text = "더보기"
                    isTextExpanded = false
                }
            }
        }

        private fun controlVideoPlayer(
            player: ExoPlayer?,
            playIcon: ImageView,
            pauseIcon: ImageView
        ) {
            player?.let {
                playIcon.visibility = View.GONE
                pauseIcon.visibility = View.GONE

                binding.root.setOnClickListener {
                    if (player.isPlaying) {
                        player.pause()
                        setIconWithAnimation(pauseIcon)
                    } else {
                        player.play()
                        setIconWithAnimation(playIcon)
                    }
                }
            }
        }

        private fun setIconWithAnimation(icon: ImageView) {
            icon.visibility = View.VISIBLE
            icon.alpha = 1.0f

            val animator = ObjectAnimator.ofFloat(icon, View.ALPHA, 1.0f, 0.0f)
            animator.duration = 1500

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    icon.visibility = View.GONE
                }
            })

            animator.start()
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeforeAfterDetailAdapter.BeforeAfterDetailViewHolder {
        val binding = FragmentBeforeAfterDetailItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BeforeAfterDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeforeAfterDetailViewHolder, position: Int) {
        holder.bind(contents[position])
    }

    override fun getItemCount(): Int = contents.size
}
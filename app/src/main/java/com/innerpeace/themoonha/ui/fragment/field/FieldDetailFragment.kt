package com.innerpeace.themoonha.ui.fragment.field

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.field.FieldDetailResponse
import com.innerpeace.themoonha.data.repository.FieldRepository
import com.innerpeace.themoonha.databinding.FragmentFieldDetailBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.FieldViewModel
import com.innerpeace.themoonha.viewModel.factory.FieldViewModelFactory

/**
 * Field Detail 프래그먼트
 * @author 김진규
 * @since 2024.09.05
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.05  	김진규       최초 생성
 * </pre>
 */
class FieldDetailFragment : Fragment() {
    private var _binding: FragmentFieldDetailBinding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null
    private var isTextExpanded = false
    private val viewModel: FieldViewModel by activityViewModels {
        FieldViewModelFactory(FieldRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFieldDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.apply {
            hideToolbar()
            hideBottomNavigation()
        }

        binding.backButton.setColorFilter(Color.WHITE)
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        val fieldId = arguments?.getLong("fieldId") ?: return

        viewModel.clearFieldDetail()
        viewModel.getFieldDetail(fieldId)
        viewModel.fieldDetailContent.asLiveData()
            .distinctUntilChanged()
            .observe(viewLifecycleOwner) { response ->
            if (response != null) {
                bindContent(response)
            }
        }
    }

    private fun bindContent(content: FieldDetailResponse) {
        binding.titleDetail.text = content.title
        binding.memberNameDetail.text = content.memberName

        Glide.with(this)
            .load(content.profileImgUrl)
            .circleCrop()
            .into(binding.profileImageDetail)

        if (content.contentIsImage == 1) {
            binding.imageDetail.visibility = View.VISIBLE
            binding.videoDetail.visibility = View.GONE

            Glide.with(binding.root.context)
                .load(content.contentUrl)
                .into(binding.imageDetail)
        } else {
            binding.imageDetail.visibility = View.GONE
            binding.videoDetail.visibility = View.VISIBLE

            player = ExoPlayer.Builder(requireContext()).build().apply {
                setMediaItem(MediaItem.fromUri(content.contentUrl))
                prepare()
                playWhenReady = true
                repeatMode = ExoPlayer.REPEAT_MODE_ALL
            }
            binding.videoDetail.player = player
            binding.videoDetail.useController = false
            controlVideoPlayer(player)
        }
        setupTextContent(content)
        setupHashtags(content.hashtags)
    }

    private fun setupTextContent(content: FieldDetailResponse) {
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
                isTextExpanded = true
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

    private fun controlVideoPlayer(player: ExoPlayer?) {
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

    private fun setupHashtags(hashtags: List<String>) {
        if (hashtags.isNullOrEmpty()) return
        val flow = binding.root.findViewById<Flow>(R.id.hashtagFlow)
        val idList = mutableListOf<Int>()

        for (hashtag in hashtags) {
            val textView = TextView(binding.root.context).apply {
                id = View.generateViewId()
                text = "#$hashtag"
                setTextColor(ContextCompat.getColor(binding.root.context, android.R.color.white))
                setPadding(0, 4, 8, 4)
                textSize = 12f
                layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
            }
            binding.root.addView(textView)
            idList.add(textView.id)
        }
        flow.referencedIds = idList.toIntArray()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
        _binding = null
    }
}
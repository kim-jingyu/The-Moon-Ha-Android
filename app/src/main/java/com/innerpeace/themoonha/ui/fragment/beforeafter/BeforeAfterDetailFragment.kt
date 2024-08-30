package com.innerpeace.themoonha.ui.fragment.beforeafter

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterContent
import com.innerpeace.themoonha.databinding.FragmentBeforeAfterDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Before&After Detail 프래그먼트
 * @author 김진규
 * @since 2024.08.26
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.26  	김진규       최초 생성
 * </pre>
 */
class BeforeAfterDetailFragment : Fragment() {
    private var _binding: FragmentBeforeAfterDetailBinding? = null
    private val binding get() = _binding!!

    private var isTextExpanded = false

    private var beforePlayer: ExoPlayer? = null
    private var afterPlayer: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBeforeAfterDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.findViewById<Toolbar>(R.id.toolbar)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.visibility = View.GONE

        binding.backButton.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white))
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        arguments?.getParcelable<BeforeAfterContent>("beforeAfterContent")?.let {
            lifecycleScope.launch {
                setupBeforeContent(it)
                setupAfterContent(it)
                setupTextContent(it)
                setupHashtags(it.hashtags)
            }
        }
    }

    private fun setupHashtags(hashtags: List<String>) {
        val flow = binding.root.findViewById<Flow>(R.id.hashtagFlow)
        val idList = mutableListOf<Int>()

        for (hashtag in hashtags) {
            val textView = TextView(requireContext()).apply {
                id = View.generateViewId()
                text = "#$hashtag"
                setTextColor(ContextCompat.getColor(context, android.R.color.white))
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

    private suspend fun setupTextContent(content: BeforeAfterContent) = withContext(Dispatchers.Main) {
        Glide.with(this@BeforeAfterDetailFragment)
            .load(content.profileImageUrl)
            .circleCrop()
            .error(R.drawable.ic_zzang9)
            .into(binding.profileImageDetail)
        binding.memberNameDetail.text = content.memberName
        binding.titleDetail.text = content.title

        binding.titleDetail.post {
            if (binding.titleDetail.layout.getEllipsisCount(0) > 0) {
                binding.moreButton.visibility = View.VISIBLE
            } else {
                binding.moreButton.visibility = View.GONE
            }
        }

        binding.moreButton.setOnClickListener {
            binding.titleDetail.maxLines = Int.MAX_VALUE
            binding.titleDetail.ellipsize = null
            binding.moreButton.visibility = View.GONE
            isTextExpanded = !isTextExpanded
        }

        binding.titleDetail.setOnClickListener {
            if (isTextExpanded) {
                binding.titleDetail.maxLines = 1
                binding.titleDetail.ellipsize = TextUtils.TruncateAt.END
                binding.moreButton.text = "더보기"
                binding.moreButton.visibility = View.VISIBLE
                isTextExpanded = false
            }
        }
    }

    private suspend fun setupAfterContent(content: BeforeAfterContent) = withContext(Dispatchers.Main) {
        val afterImageParams = binding.afterImageDetail.layoutParams as ConstraintLayout.LayoutParams
        val afterVideoParams = binding.afterVideoDetail.layoutParams as ConstraintLayout.LayoutParams

        if (content.afterIsImage) {
            binding.afterImageDetail.visibility = View.VISIBLE
            binding.afterVideoDetail.visibility = View.GONE
            Glide.with(this@BeforeAfterDetailFragment)
                .load(content.afterUrl)
                .into(binding.afterImageDetail)

            afterImageParams.height = 0
            afterImageParams.matchConstraintPercentHeight = 0.5f
        } else {
            binding.afterImageDetail.visibility = View.GONE
            binding.afterVideoDetail.visibility = View.VISIBLE

            afterPlayer = ExoPlayer.Builder(requireContext())
                .setRenderersFactory(DefaultRenderersFactory(requireContext()).setExtensionRendererMode(
                    EXTENSION_RENDERER_MODE_OFF
                ))
                .build().apply {
                setMediaItem(MediaItem.fromUri(content.afterUrl))
                prepare()
                playWhenReady = true
                repeatMode = ExoPlayer.REPEAT_MODE_ONE
            }
            binding.afterVideoDetail.player = afterPlayer
            binding.afterVideoDetail.useController = false

            afterVideoParams.height = 0
            afterVideoParams.matchConstraintPercentHeight = 0.5f
        }

        binding.afterImageDetail.layoutParams = afterImageParams
        binding.afterVideoDetail.layoutParams = afterVideoParams
    }

    private suspend fun setupBeforeContent(content: BeforeAfterContent) = withContext(Dispatchers.Main) {
        val beforeImageParams = binding.beforeImageDetail.layoutParams as ConstraintLayout.LayoutParams
        val beforeVideoParams = binding.beforeVideoDetail.layoutParams as ConstraintLayout.LayoutParams
        val afterImageParams = binding.afterImageDetail.layoutParams as ConstraintLayout.LayoutParams
        val afterVideoParams = binding.afterVideoDetail.layoutParams as ConstraintLayout.LayoutParams

        if (content.beforeIsImage) {
            binding.beforeImageDetail.visibility = View.VISIBLE
            binding.beforeVideoDetail.visibility = View.GONE
            Glide.with(this@BeforeAfterDetailFragment)
                .load(content.beforeUrl)
                .into(binding.beforeImageDetail)

            beforeImageParams.height = 0
            beforeImageParams.matchConstraintPercentHeight = 0.5f

            afterImageParams.topToBottom = binding.beforeImageDetail.id
            afterVideoParams.topToBottom = binding.beforeImageDetail.id
        } else {
            binding.beforeImageDetail.visibility = View.GONE
            binding.beforeVideoDetail.visibility = View.VISIBLE

            beforePlayer = ExoPlayer.Builder(requireContext())
                .setRenderersFactory(DefaultRenderersFactory(requireContext()).setExtensionRendererMode(EXTENSION_RENDERER_MODE_OFF))
                .build().apply {
                    setMediaItem(MediaItem.fromUri(content.beforeUrl))
                    prepare()
                    playWhenReady = true
                    repeatMode = ExoPlayer.REPEAT_MODE_ONE
                }
            binding.beforeVideoDetail.player = beforePlayer
            binding.afterVideoDetail.useController = false

            beforeVideoParams.height = 0
            beforeVideoParams.matchConstraintPercentHeight = 0.5f

            afterImageParams.topToBottom = binding.beforeVideoDetail.id
            afterVideoParams.topToBottom = binding.beforeVideoDetail.id
        }

        binding.beforeImageDetail.layoutParams = beforeImageParams
        binding.beforeVideoDetail.layoutParams = beforeVideoParams
        binding.afterImageDetail.layoutParams = afterImageParams
        binding.afterVideoDetail.layoutParams = afterVideoParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        beforePlayer?.release()
        afterPlayer?.release()

        activity?.findViewById<Toolbar>(R.id.toolbar)?.visibility = View.VISIBLE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.visibility = View.VISIBLE
        _binding = null
    }
}


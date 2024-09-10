package com.innerpeace.themoonha.ui.fragment.field

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.field.FieldDetailResponse
import com.innerpeace.themoonha.databinding.FragmentFieldDetailBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private var isTextExpanded = false
    private var player: ExoPlayer? = null

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

        binding.backButton.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white))
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        arguments?.getParcelable<FieldDetailResponse>("fieldDetailResponse")?.let {
            lifecycleScope.launch {
                setupContent(it)
                setupTextContent(it)
                setupHashtags(it.hashtags)
            }
        }
    }

    private fun setupHashtags(hashtags: List<String>) {
        if (hashtags == null || hashtags.isEmpty()) return
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

    private suspend fun setupTextContent(content: FieldDetailResponse) = withContext(
        Dispatchers.Main) {
        Glide.with(this@FieldDetailFragment)
            .load(content.profileImgUrl)
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

    private suspend fun setupContent(content: FieldDetailResponse) = withContext(
        Dispatchers.Main) {
        val imageParams = binding.imageDetail.layoutParams as ConstraintLayout.LayoutParams
        val videoParams = binding.videoDetail.layoutParams as ConstraintLayout.LayoutParams

        if (content.contentIsImage == 1) {
            binding.imageDetail.visibility = View.VISIBLE
            binding.videoDetail.visibility = View.GONE
            Glide.with(this@FieldDetailFragment)
                .load(content.contentUrl)
                .into(binding.imageDetail)
        } else {
            binding.imageDetail.visibility = View.GONE
            binding.videoDetail.visibility = View.VISIBLE

            Log.d("FieldDetailFragment", "Video URL: ${content.contentUrl}")

            player = ExoPlayer.Builder(requireContext())
                .setRenderersFactory(
                    DefaultRenderersFactory(requireContext()).setExtensionRendererMode(
                    DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
                ))
                .build().apply {
                    setMediaItem(MediaItem.fromUri(content.contentUrl))
                    prepare()
                    playWhenReady = true
                    repeatMode = ExoPlayer.REPEAT_MODE_ONE
                }
            binding.videoDetail.player = player
            binding.videoDetail.useController = false
        }

        binding.imageDetail.layoutParams = imageParams
        binding.videoDetail.layoutParams = videoParams
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()

        activity?.findViewById<Toolbar>(R.id.toolbar)?.visibility = View.VISIBLE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.visibility = View.VISIBLE
        _binding = null
    }
}
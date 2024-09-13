package com.innerpeace.themoonha.ui.fragment.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.innerpeace.themoonha.BuildConfig.BASE_IP_ADDRESS
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.live.LiveLessonDetailResponse
import com.innerpeace.themoonha.data.repository.LiveRepository
import com.innerpeace.themoonha.databinding.FragmentLiveStreamingMainBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.LiveViewModel
import com.innerpeace.themoonha.viewModel.factory.LiveViewModelFactory
import kotlinx.coroutines.launch

/**
 * 실시간 강좌 - 스트리밍 메인 페이지 프래그먼트
 * @author 김진규
 * @since 2024.09.06
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.06  	김진규       최초 생성
 * </pre>
 */
class LiveStreamingMainFragment : Fragment() {
    private var _binding: FragmentLiveStreamingMainBinding? = null
    private val binding get() = _binding!!
    private var player: ExoPlayer? = null
    private val viewModel: LiveViewModel by viewModels {
        LiveViewModelFactory(LiveRepository())
    }

    private var liveId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLiveStreamingMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.apply {
            hideBottomNavigation()
        }

        val resp: LiveLessonDetailResponse = arguments?.getParcelable("liveLessonDetailResponse")!!

        liveId = resp.liveId
        lifecycleScope.launch { viewModel.joinLiveLesson(liveId) }
        setOnAirPlayer(resp.broadcastUrl)

        val mainInfoFragment = LiveStreamingMainInfoFragment().apply {
            arguments = Bundle().apply {
                putParcelable("liveLessonDetailResponse", resp)
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.live_streaming_info_container, mainInfoFragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        releaseOnAirPlayer()
    }

    private fun releaseOnAirPlayer() {
        player?.release()
        player = null
        lifecycleScope.launch { viewModel.leaveLiveLesson(liveId) }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onResume() {
        super.onResume()
        player?.play()
    }

    private fun setOnAirPlayer(streamUrl: String) {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.onAirPlayer.player = player
        val mediaItem = MediaItem.fromUri(streamUrl.replace("localhost", BASE_IP_ADDRESS))
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }
}
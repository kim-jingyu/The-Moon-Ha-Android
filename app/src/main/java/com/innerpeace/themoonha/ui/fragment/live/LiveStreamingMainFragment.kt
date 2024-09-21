package com.innerpeace.themoonha.ui.fragment.live

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.innerpeace.themoonha.BuildConfig.STREAM_ID
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.live.LiveLessonDetailResponse
import com.innerpeace.themoonha.data.repository.LiveRepository
import com.innerpeace.themoonha.databinding.FragmentLiveStreamingMainBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.LiveViewModel
import com.innerpeace.themoonha.viewModel.factory.LiveViewModelFactory
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.SupervisorJob
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
    private val viewModel: LiveViewModel by viewModels {
        LiveViewModelFactory(LiveRepository())
    }

    private var liveId: Long = 0
    private lateinit var mRtcEngine: RtcEngine
    private val appId = STREAM_ID
    private val token: String? = null
    private var elapsedTime: Long = 0L
    private var updateHandler: Handler? = null
    private var updateRunnable: Runnable? = null
    private val customScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        initializeAgora()
        joinChannel()

        setupSeekBar()

        val startTime = System.currentTimeMillis()
        updateHandler = Handler(Looper.getMainLooper())
        updateRunnable = object : Runnable {
            override fun run() {
                elapsedTime = (System.currentTimeMillis() - startTime) / 1000
                binding.videoSeekBar.progress = elapsedTime.toInt()
                updateHandler?.postDelayed(this, 1000)
            }
        }
        updateHandler?.post(updateRunnable!!)

        val mainInfoFragment = LiveStreamingMainInfoFragment().apply {
            arguments = Bundle().apply {
                putParcelable("liveLessonDetailResponse", resp)
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.live_streaming_info_container, mainInfoFragment)
            .commit()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterFullScreen();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            exitFullScreen();
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        customScope.launch {
            viewModel.leaveLiveLesson(liveId)
        }
        mRtcEngine.leaveChannel()
        RtcEngine.destroy()
        updateHandler?.removeCallbacks(updateRunnable!!)
        updateHandler = null
        updateRunnable = null
    }

    private fun enterFullScreen() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding.onAirPlayer.layoutParams = ConstraintLayout.LayoutParams (
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        binding.onAirPlayer.requestLayout()
    }

    private fun exitFullScreen() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        binding.onAirPlayer.layoutParams = ConstraintLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        binding.onAirPlayer.requestLayout()
    }

    private fun setupSeekBar() {
        binding.videoSeekBar.max = 3600
        binding.videoSeekBar.progress = 0
    }

    private fun initializeAgora() {
        try {
            mRtcEngine = RtcEngine.create(requireContext(), appId, mRtcEventHandler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mRtcEngine.enableVideo()
        setupLocalVideo()
    }

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            activity?.runOnUiThread {
                setupRemoteVideo(uid)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            activity?.runOnUiThread {
                showStreamEndedMessage()
            }
        }
    }

    private fun showStreamEndedMessage() {
        binding.onAirPlayer.removeAllViews()
        binding.onAirPlayer.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.black))
        binding.onAirPlayer.addView(TextView(requireContext()).apply {
            text = "방송이 종료되었습니다."
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            gravity = Gravity.CENTER
            textSize = 20f
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        })
    }

    private fun setupLocalVideo() {
        val surfaceView = RtcEngine.CreateRendererView(requireContext())
        surfaceView.setZOrderMediaOverlay(true)
        binding.onAirPlayer.addView(surfaceView)
        mRtcEngine.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
    }

    private fun setupRemoteVideo(uid: Int) {
        val surfaceView = RtcEngine.CreateRendererView(requireContext())
        surfaceView.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        surfaceView.setZOrderMediaOverlay(true)
        binding.onAirPlayer.addView(surfaceView)
        mRtcEngine.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
    }

    private fun joinChannel() {
        mRtcEngine.joinChannel(token, liveId.toString(), "", 0)
    }
}
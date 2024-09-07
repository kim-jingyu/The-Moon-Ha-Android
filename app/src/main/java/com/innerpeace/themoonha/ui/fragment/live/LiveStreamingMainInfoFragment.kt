package com.innerpeace.themoonha.ui.fragment.live

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.data.model.live.LiveLessonDetailResponse
import com.innerpeace.themoonha.databinding.FragmentLiveStreamingMainInfoBinding

class LiveStreamingMainInfoFragment: Fragment() {
    private var _binding: FragmentLiveStreamingMainInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLiveStreamingMainInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val liveLessonDetail: LiveLessonDetailResponse = it.getParcelable("liveLessonDetailResponse")!!
            updateMainInfoUI(liveLessonDetail)
        }
    }

    private fun updateMainInfoUI(resp: LiveLessonDetailResponse) {
        binding.liveStreamTitle.text = resp.title
        binding.viewerCountAndTime.text = "2.2명 시청 중 시작: ${resp.minutesAgo}분 전"
        binding.profileName.text = "${resp.instructorName} 강사님"

        Glide.with(this)
            .load(resp.profileImgUrl)
            .into(binding.profileImageMain)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
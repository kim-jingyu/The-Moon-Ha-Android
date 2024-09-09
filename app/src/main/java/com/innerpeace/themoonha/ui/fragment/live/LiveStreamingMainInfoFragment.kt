package com.innerpeace.themoonha.ui.fragment.live

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.data.model.live.LiveLessonDetailResponse
import com.innerpeace.themoonha.data.repository.LiveRepository
import com.innerpeace.themoonha.databinding.FragmentLiveStreamingMainInfoBinding
import com.innerpeace.themoonha.viewModel.LiveViewModel
import com.innerpeace.themoonha.viewModel.factory.LiveViewModelFactory
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link

/**
 * 실시간 강좌 - 스트리밍 메인 페이지 정보 프래그먼트
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
class LiveStreamingMainInfoFragment: Fragment() {
    private var _binding: FragmentLiveStreamingMainInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LiveViewModel by viewModels {
        LiveViewModelFactory(LiveRepository())
    }

    private var liveId: Long = 0
    private var title: String = ""
    private var description: String = ""
    private var imageUrl: String = ""

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
            liveId = liveLessonDetail.liveId
            title = liveLessonDetail.title
            description = liveLessonDetail.thumbnailUrl
            updateMainInfoUI(liveLessonDetail)
        }

        viewModel.getViewersCount(liveId)
        viewModel.getLikesCount(liveId)

        binding.likeButton.setOnClickListener {
            viewModel.likeLiveLesson(liveId)
        }

        binding.shareButton.setOnClickListener {
            viewModel.getShareLink(liveId)
        }

        viewModel.liveLessonLikesCountResponse.asLiveData().observe(viewLifecycleOwner) { likeCount ->
            binding.likeCount.text = likeCount?.toString() ?: "0"
        }

        viewModel.liveLessonViewersCountResponse.asLiveData().observe(viewLifecycleOwner) { viewerCount ->
            binding.viewerCount.text = "${viewerCount ?: 0}명 시청 "
        }

        viewModel.liveLessonDetailResponse.asLiveData().observe(viewLifecycleOwner) { detailResponse ->
            detailResponse?.let {
                binding.time.text = "시작: ${detailResponse.minutesAgo}분 전"
            }
        }

        viewModel.liveLessonShareLinkResponse.asLiveData().observe(viewLifecycleOwner) { shareLink ->
            if (shareLink.isNotEmpty()) {
                shareKakaoLink(shareLink)
            }
        }
    }

    private fun shareKakaoLink(shareLink: String) {
        val feedTemplate = FeedTemplate(
            content = Content(
                title = title,
                description = description,
                imageUrl = imageUrl,
                link = Link(
                    webUrl = shareLink,
                    mobileWebUrl = shareLink
                )
            ),
            buttons = listOf(
                Button(
                    title = "라이브 스트리밍 시청",
                    link = Link(
                        mobileWebUrl = shareLink,
                        webUrl = shareLink
                    )
                )
            )
        )

        if (ShareClient.instance.isKakaoTalkSharingAvailable(requireContext())) {
            ShareClient.instance.shareDefault(requireContext(), feedTemplate) { sharingResult, error ->
                if (error != null) {
                    Log.e("KakaoShare", "카카오톡 메시지 전송 실패: ${error.message}")
                } else if (sharingResult != null) {
                    Log.i("KakaoShare", "카카오톡 메시지 전송 성공: ${sharingResult.intent}")
                    context?.startActivity(sharingResult.intent)
                }
            }
        } else {
            Log.w("KakaoShare", "카카오톡이 설치되어 있지 않음. 웹 브라우저로 공유합니다.")
            ShareClient.instance.shareDefault(requireContext(), feedTemplate) { sharingResult, error ->
                if (error != null) {
                    Log.e("KakaoShare", "웹 브라우저를 통한 카카오톡 메시지 전송 실패: ${error.message}")
                } else if (sharingResult != null) {
                    Log.i("KakaoShare", "웹 브라우저를 통한 카카오톡 메시지 전송 성공: ${sharingResult.intent}")
                }
            }
        }
    }

    private fun updateMainInfoUI(resp: LiveLessonDetailResponse) {
        binding.liveStreamTitle.text = resp.title
        binding.viewerCount.text = "0명 시청 중"
        binding.time.text = "시작: ${resp.minutesAgo}분 전"
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
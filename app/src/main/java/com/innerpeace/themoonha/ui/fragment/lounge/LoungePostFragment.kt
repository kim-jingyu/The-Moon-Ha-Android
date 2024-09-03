package com.innerpeace.themoonha.ui.fragment.lounge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.innerpeace.themoonha.adapter.lounge.LoungeCommentViewAdapter
import com.innerpeace.themoonha.adapter.lounge.LoungeHomePostViewAdapter
import com.innerpeace.themoonha.data.model.lounge.LoungePostResponse
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LoungeService
import com.innerpeace.themoonha.data.repository.LoungeRepository
import com.innerpeace.themoonha.databinding.FragmentLoungePostBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.LoungeViewModel
import com.innerpeace.themoonha.viewModel.factory.LoungeViewModelFactory

/**
 * 라운지 게시글 상세 프래그먼트
 * @author 조희정
 * @since 2024.08.23
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.24  	조희정       최초 생성
 * 2024.09.02   조희정       이미지, 댓글 recycler view 구현
 * </pre>
 */
class LoungePostFragment : Fragment() {
    private var _binding: FragmentLoungePostBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LoungeCommentViewAdapter
    private val viewModel: LoungeViewModel by activityViewModels {
        LoungeViewModelFactory(LoungeRepository(ApiClient.getClient().create(LoungeService::class.java)))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoungePostBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 불러오기
        viewModel.selectedLoungeId.observe(viewLifecycleOwner, Observer { loungeId ->
            if (loungeId != null) {
                viewModel.selectedLoungePostId.observe(viewLifecycleOwner, Observer { postId ->
                    if (postId != null) {
                        viewModel.fetchPostDetail(loungeId, postId)
                    }
                })
            }
        })

        viewModel.postDetail.observe(viewLifecycleOwner, Observer { postDetail ->
            postDetail?.let {
                Glide.with(binding.ivProfileImage.context)
                    .load(postDetail.loungePost.loungeMember.profileImgUrl)
                    .circleCrop()
                    .into(binding.ivProfileImage)
                binding.tvName.text = postDetail.loungePost.loungeMember.name
                binding.tvDate.text = postDetail.loungePost.createdAt
                addImagesToLayout(it.loungePost.loungePostImgList)
                setupCommentRecyclerView(it.loungeCommentList)
            }
        })
    }
    
    // 게시글 이미지 추가
    private fun addImagesToLayout(imageUrls: List<String>) {
        for (imageUrl in imageUrls) {
            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 8.dpToPx()) // 이미지 간 8dp의 마진 설정
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            // Glide를 사용하여 이미지 로드
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)

            // LinearLayout에 이미지 추가
            binding.llImages.addView(imageView)
        }
    }

    // dp 값을 px 값으로 변환하는 함수
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    // 댓글 Recycler View
    private fun setupCommentRecyclerView(item: List<LoungePostResponse.LoungeComment>) {
        adapter = LoungeCommentViewAdapter()
        binding.rvCommentList.adapter = adapter

        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rvCommentList.layoutManager = linearLayoutManager

        adapter.setItems(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
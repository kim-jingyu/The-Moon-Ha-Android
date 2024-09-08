package com.innerpeace.themoonha.ui.fragment.lesson

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.icu.text.DecimalFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.lesson.CartRequest
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LessonService
import com.innerpeace.themoonha.data.repository.LessonRepository
import com.innerpeace.themoonha.databinding.FragmentLessonDetailBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.LessonViewModel
import com.innerpeace.themoonha.viewModel.factory.LessonViewModelFactory

class LessonDetailFragment : Fragment() {
    private var _binding: FragmentLessonDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LessonViewModel by activityViewModels {
        LessonViewModelFactory(LessonRepository(ApiClient.getClient().create(LessonService::class.java)))
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isVideoPlaying = false
    private var isVideoPaused = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLessonDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        (activity as? MainActivity)?.hideNavigationBar()
        (activity as? MainActivity)?.setToolbarTitle("강좌 상세")

        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lessonId = arguments?.getLong("lessonId") ?: return
        Log.i("lessonId = ", lessonId.toString())
        viewModel.getLessonDetail(lessonId)

        viewModel.lessonDetail.observe(viewLifecycleOwner, Observer { lessonDetail ->
            lessonDetail?.let {
                binding.textViewTitle.text = it.title
                binding.textViewBranchName.text = it.branchName
                binding.textViewPeriod.text = it.period
                binding.textViewLessonTime.text = it.lessonTime
                binding.textViewCnt.text = "${it.cnt}회"
                binding.textViewPlace.text = it.place
                binding.textViewTutorName.text = it.tutorName
                binding.textViewCost.text = formatCurrency(it.cost)
                binding.textViewSummary.text = it.summary
                binding.textViewCurriculum.text = it.curriculum
                binding.textViewSupply.text = it.supply

                if (it.onlineCost == null) {
                    binding.textViewOnlineCost.text = "온라인 지원 X"
                } else {
                    binding.textViewOnlineCost.text = formatCurrency(it.onlineCost)
                }

                binding.textViewTutorName2.text = it.tutorName

                Glide.with(this)
                    .load(it.thumbnailUrl)
                    .into(binding.imageViewThumbnail)

                Glide.with(this)
                    .load(it.tutorProfileImgUrl)
                    .into(binding.imageViewTutorProfile)

                setupVideo(it.previewVideoUrl)
            }
        })

        setupTabLayout()

        binding.addToCartButton.setOnClickListener {
            showOnlineYnSelectionDialog()
        }

        binding.scrollView.post {
            binding.scrollView.scrollTo(0, 0)
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("강좌소개"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("커리큘럼"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("준비물"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("강사정보"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("후기"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> scrollToSection(binding.textViewSummary)
                    1 -> scrollToSection(binding.textViewCurriculum)
                    2 -> scrollToSection(binding.textViewSupply)
                    3 -> scrollToSection(binding.textViewTutor)
                    4 -> scrollToSection(binding.textViewReviewDetails)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                onTabSelected(tab)
            }
        })
    }

    private fun scrollToSection(view: View) {
        binding.scrollView.post {
            val scrollY = view.top - binding.scrollView.top
            binding.scrollView.scrollTo(0, scrollY)
        }
    }

    private val videoPlayRunnable = Runnable {
        if (!isVideoPlaying && viewModel.lessonDetail.value?.previewVideoUrl != null) {
            binding.imageViewThumbnail.visibility = View.GONE
            binding.videoView.visibility = View.VISIBLE
            setupVideo(viewModel.lessonDetail.value?.previewVideoUrl!!)
            isVideoPlaying = true
        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(videoPlayRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(videoPlayRunnable)
        if (isVideoPlaying) {
            binding.videoView.stopPlayback()
            binding.imageViewThumbnail.visibility = View.VISIBLE
            binding.videoView.visibility = View.GONE
            isVideoPlaying = false
        }
    }

    private fun setupVideo(videoUrl: String) {
        binding.videoView.setVideoPath(videoUrl)

        binding.videoView.setOnPreparedListener { mediaPlayer ->
            binding.videoView.start()
            isVideoPlaying = true
        }

        val playIcon = binding.playIcon
        val pauseIcon = binding.pauseIcon

        binding.videoView.setOnClickListener {
            if (isVideoPlaying && !isVideoPaused) {
                binding.videoView.pause()
                isVideoPaused = true
                showIconWithAnimation(pauseIcon)
            } else if (isVideoPaused) {
                binding.videoView.start()
                isVideoPaused = false
                showIconWithAnimation(playIcon)
            }
        }

        binding.videoView.setOnCompletionListener {
            binding.videoView.visibility = View.GONE
            binding.imageViewThumbnail.visibility = View.VISIBLE
            isVideoPlaying = false
            isVideoPaused = false
        }
    }

    private fun showIconWithAnimation(icon: ImageView) {
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

    private fun showOnlineYnSelectionDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_lesson_detail_dialog, null)
        bottomSheetDialog.setContentView(dialogView)

        val btnCompleteSelection = dialogView.findViewById<AppCompatButton>(R.id.btnCompleteSelection)
        val btnOffline = dialogView.findViewById<AppCompatButton>(R.id.branchHyundaiSeoul)
        val btnOnline = dialogView.findViewById<AppCompatButton>(R.id.branchJungdong)

        btnCompleteSelection.isEnabled = false

        var isOfflineSelected = false
        var isOnlineSelected = false

        btnOffline.setOnClickListener {
            isOfflineSelected = true
            isOnlineSelected = false

            btnCompleteSelection.isEnabled = true
            btnCompleteSelection.setBackgroundColor(resources.getColor(R.color.black))

            btnOffline.isSelected = true
            btnOnline.isSelected = false
        }

        btnOnline.setOnClickListener {
            isOnlineSelected = true
            isOfflineSelected = false

            btnCompleteSelection.isEnabled = true
            btnCompleteSelection.setBackgroundColor(resources.getColor(R.color.black))

            btnOnline.isSelected = true
            btnOffline.isSelected = false
        }

        if (btnCompleteSelection.isEnabled) {
            btnCompleteSelection.setBackgroundColor(resources.getColor(R.color.black))
        } else {
            btnCompleteSelection.setBackgroundColor(resources.getColor(R.color.gray))
        }

        btnCompleteSelection.setOnClickListener {
            val onlineYn = isOnlineSelected
            val lessonId = arguments?.getLong("lessonId") ?: return@setOnClickListener

            val cartRequest = CartRequest(lessonId, onlineYn)

            viewModel.addLessonCart(cartRequest).observe(viewLifecycleOwner, Observer { success ->
                if (success) {
                    findNavController().navigate(R.id.action_lessonDetailFragment_to_fragment_cart)
                    bottomSheetDialog.dismiss()
                } else {
                    Log.e("LessonDetailFragment", "장바구니 추가 실패")
                }
            })
        }

        bottomSheetDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun formatCurrency(value: Int): String {
        val decimalFormat = DecimalFormat("#,###")
        return decimalFormat.format(value) + "원"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(videoPlayRunnable)
        _binding = null
    }
}

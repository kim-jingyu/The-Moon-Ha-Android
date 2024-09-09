package com.innerpeace.themoonha.ui.fragment.craft

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.PrologueThemeAdapter
import com.innerpeace.themoonha.adapter.SuggestionAdapter
import com.innerpeace.themoonha.data.model.craft.PageDTO
import com.innerpeace.themoonha.data.model.craft.PrologueDTO
import com.innerpeace.themoonha.data.model.craft.PrologueThemeDTO
import com.innerpeace.themoonha.data.model.craft.SuggestionRequest
import com.innerpeace.themoonha.data.model.craft.WishLessonDTO
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.CraftService
import com.innerpeace.themoonha.data.repository.CraftRepository
import com.innerpeace.themoonha.databinding.FragmentCraftBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.CraftViewModel
import com.innerpeace.themoonha.viewModel.factory.CraftViewModelFactory


class CraftFragment : Fragment() {
    private var _binding: FragmentCraftBinding? = null
    private val binding get() = _binding!!
    private val craftViewModel: CraftViewModel by activityViewModels {
        CraftViewModelFactory(
            CraftRepository(ApiClient.getClient().create(CraftService::class.java))
        )
    }
    private var currentPage = 1
    private var totalPages = 0
    private var hasVotedFirstLessons: Boolean = false
    private var hasVotedSecondLessons: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.findViewById<Toolbar>(R.id.toolbar)?.visibility = View.VISIBLE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.visibility =
            View.VISIBLE
        _binding = FragmentCraftBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as? MainActivity)?.setToolbarTitle("문화공방")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupCommentInput()
    }

    private fun setupRecyclerView() {
        binding.prologueRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        craftViewModel.craftMainResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                val themeList = convertToPrologueThemeDTOList(it.prologueList)
                val adapter = PrologueThemeAdapter(themeList) { selectedPrologue ->
                    craftViewModel.setCurrentPrologueDetail(selectedPrologue)
                    Log.i("selectedPrologue : ", selectedPrologue.toString())
                    findNavController().navigate(R.id.action_fragment_craft_to_prologueDetailFragment)
                }
                binding.prologueRecyclerView.adapter = adapter

                populateWishLessonContainers(
                    it.firstWishLessonList,
                    it.secondWishLessonList
                )

                val suggestionAdapter = SuggestionAdapter(it.suggestionList)
                Log.i("size : ", it.suggestionList.size.toString())
                binding.suggestionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.suggestionRecyclerView.adapter = suggestionAdapter

                setupPagination(it.pageDTO)
            }
        })

        craftViewModel.getCraftMain()

        craftViewModel.toastMessage.observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                craftViewModel.clearToastMessage()
            }
        })

        craftViewModel.suggestionResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                Log.i("test : ", it.suggestionList.get(0).content)
                val suggestionAdapter = SuggestionAdapter(it.suggestionList)
                binding.suggestionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.suggestionRecyclerView.adapter = suggestionAdapter
                setupPagination(it.pageDTO)
            }
        })
    }

    private fun convertToPrologueThemeDTOList(prologueList: List<PrologueDTO>): List<PrologueThemeDTO> {
        val groupedByThemeId = prologueList.groupBy { it.prologueThemeId }

        return groupedByThemeId.map { (themeId, prologueItems) ->
            val themeName = prologueItems.firstOrNull()?.themeName ?: ""
            val themeDescription = prologueItems.firstOrNull()?.themeDescription ?: ""

            PrologueThemeDTO(
                themeName = themeName,
                themeDescription = themeDescription,
                prologueList = prologueItems
            )
        }
    }

    private fun populateWishLessonContainers(
        firstLessons: List<WishLessonDTO>,
        secondLessons: List<WishLessonDTO>
    ) {
        binding.firstWishLessonContainer.removeAllViews()
        binding.secondWishLessonContainer.removeAllViews()

        val inflater = LayoutInflater.from(requireContext())

        hasVotedFirstLessons = firstLessons.any { it.alreadyVoted }
        hasVotedSecondLessons = secondLessons.any { it.alreadyVoted }

        for (i in firstLessons.indices) {
            val lesson = firstLessons[i]
            val lessonView = inflater.inflate(
                R.layout.fragment_wish_lesson_item,
                binding.firstWishLessonContainer,
                false
            ) as ViewGroup
            val lessonTitleTextView: TextView = lessonView.findViewById(R.id.wishLessonTitle)
            lessonTitleTextView.text = lesson.title

            if (lesson.alreadyVoted) {
                lessonView.background = ContextCompat.getDrawable(requireContext(), R.drawable.wish_lesson_green_border)
                lessonTitleTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }

            lessonView.setOnClickListener {
                if (!lesson.alreadyVoted) {
                    hasVotedFirstLessons = true
                    craftViewModel.voteWishLesson(lesson.wishLessonId)
                    lesson.alreadyVoted = true
                    lesson.voteCnt += 1
                    updateProgressBars(firstLessons, secondLessons)
                    lessonView.background = ContextCompat.getDrawable(requireContext(), R.drawable.wish_lesson_green_border)
                    lessonTitleTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                }
            }

            binding.firstWishLessonContainer.addView(lessonView)

            if (i == 0 && firstLessons.size > 1) {
                val vsView = inflater.inflate(
                    R.layout.fragment_wish_lesson_vs,
                    binding.firstWishLessonContainer,
                    false
                )
                binding.firstWishLessonContainer.addView(vsView)
            }
        }

        for (i in secondLessons.indices) {
            val lesson = secondLessons[i]
            val lessonView = inflater.inflate(
                R.layout.fragment_wish_lesson_item,
                binding.secondWishLessonContainer,
                false
            ) as ViewGroup
            val lessonTitleTextView: TextView = lessonView.findViewById(R.id.wishLessonTitle)
            lessonTitleTextView.text = lesson.title

            if (lesson.alreadyVoted) {
                lessonView.background = ContextCompat.getDrawable(requireContext(), R.drawable.wish_lesson_green_border)
                lessonTitleTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }

            lessonView.setOnClickListener {
                if (!lesson.alreadyVoted) {
                    hasVotedSecondLessons = true
                    craftViewModel.voteWishLesson(lesson.wishLessonId)
                    lesson.alreadyVoted = true
                    lesson.voteCnt += 1
                    updateProgressBars(firstLessons, secondLessons)
                    lessonView.background = ContextCompat.getDrawable(requireContext(), R.drawable.wish_lesson_green_border)
                    lessonTitleTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                }
            }

            binding.secondWishLessonContainer.addView(lessonView)

            if (i == 0 && secondLessons.size > 1) {
                val vsView = inflater.inflate(
                    R.layout.fragment_wish_lesson_vs,
                    binding.secondWishLessonContainer,
                    false
                )
                binding.secondWishLessonContainer.addView(vsView)
            }
        }

        updateProgressBars(firstLessons, secondLessons)
    }

    private fun updateProgressBars(
        firstLessons: List<WishLessonDTO>,
        secondLessons: List<WishLessonDTO>
    ) {
        val progressContainerFirst = binding.progressContainerFirst
        val progressBarFirst = binding.progressBarFirst
        val percentageTextViewFirst_1 = binding.percentageTextViewFirst1
        val percentageTextViewFirst_2 = binding.percentageTextViewFirst2

        val progressContainerSecond = binding.progressContainerSecond
        val progressBarSecond = binding.progressBarSecond
        val percentageTextViewSecond_1 = binding.percentageTextViewSecond1
        val percentageTextViewSecond_2 = binding.percentageTextViewSecond2

        progressContainerFirst.visibility = if (firstLessons.any { it.voteCnt > 0 }) View.VISIBLE else View.GONE
        progressContainerSecond.visibility = if (secondLessons.any { it.voteCnt > 0 }) View.VISIBLE else View.GONE

        val totalVotesFirst = firstLessons.sumOf { it.voteCnt }
        if (totalVotesFirst > 0) {
            val percentageFirst_1 =
                (firstLessons.getOrNull(0)?.voteCnt?.toDouble()?.div(totalVotesFirst)?.times(100))?.toInt() ?: 0
            val percentageFirst_2 =
                (firstLessons.getOrNull(1)?.voteCnt?.toDouble()?.div(totalVotesFirst)?.times(100))?.toInt() ?: 0
            progressBarFirst.max = 100
            progressBarFirst.progress = percentageFirst_1

            percentageTextViewFirst_1.text = "${percentageFirst_1}%"
            percentageTextViewFirst_2.text = "${percentageFirst_2}%"

            val firstLesson2 = firstLessons.getOrNull(1)
            if (firstLesson2?.alreadyVoted == true) {
                progressBarFirst.progress = percentageFirst_2
                progressBarFirst.scaleX = -1f
            }
        } else {
            progressContainerFirst.visibility = View.GONE
        }

        if (!hasVotedFirstLessons) progressContainerFirst.visibility = View.GONE

        val totalVotesSecond = secondLessons.sumOf { it.voteCnt }
        if (totalVotesSecond > 0) {
            val percentageSecond_1 =
                (secondLessons.getOrNull(0)?.voteCnt?.toDouble()?.div(totalVotesSecond)?.times(100))?.toInt() ?: 0
            val percentageSecond_2 =
                (secondLessons.getOrNull(1)?.voteCnt?.toDouble()?.div(totalVotesSecond)?.times(100))?.toInt() ?: 0

            progressBarSecond.max = 100
            progressBarSecond.progress = percentageSecond_1

            percentageTextViewSecond_1.text = "${percentageSecond_1}%"
            percentageTextViewSecond_2.text = "${percentageSecond_2}%"

            // 투표한 항목의 색상 변경
            val secondLesson2 = secondLessons.getOrNull(1)
            if (secondLesson2?.alreadyVoted == true) {
                progressBarSecond.progress = percentageSecond_2
                progressBarSecond.scaleX = -1f
            }
        } else {
            progressContainerSecond.visibility = View.GONE
        }

        if (!hasVotedSecondLessons) progressContainerSecond.visibility = View.GONE
    }

    private fun setupPagination(pageDTO: PageDTO) {
        binding.pageNumberContainer.removeAllViews()

        totalPages = pageDTO.realEnd
        val startPage = pageDTO.startPage
        val endPage = pageDTO.endPage

        binding.prevPageButton.isEnabled = pageDTO.prev
        binding.prevPageButton.setColorFilter(
            if (pageDTO.prev) ContextCompat.getColor(requireContext(), R.color.light_green)
            else ContextCompat.getColor(requireContext(), R.color.gray),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        binding.prevPageButton.setOnClickListener {
            if (pageDTO.prev) {
                currentPage = startPage - 1
                craftViewModel.getSuggestionList(currentPage)
            }
        }

        for (i in startPage..endPage) {
            val pageButton = TextView(requireContext()).apply {
                text = i.toString()
                setPadding(8.dpToPx(), 8.dpToPx(), 8.dpToPx(), 8.dpToPx())
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                setOnClickListener {
                    currentPage = i
                    craftViewModel.getSuggestionList(currentPage)
                }
            }

            if (i == currentPage) {
                pageButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_green))
                pageButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            binding.pageNumberContainer.addView(pageButton)
        }

        // Add next button
        binding.nextPageButton.isEnabled = pageDTO.next
        binding.nextPageButton.setColorFilter(
            if (pageDTO.next) ContextCompat.getColor(requireContext(), R.color.light_green)
            else ContextCompat.getColor(requireContext(), R.color.gray),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        binding.nextPageButton.setOnClickListener {
            if (pageDTO.next) {
                currentPage = endPage + 1
                craftViewModel.getSuggestionList(currentPage)
            }
        }
    }

    private fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }

    private fun setupCommentInput() {
        val commentEditText = binding.commentEditText
        val sendButton = binding.sendButton

        sendButton.setOnClickListener {
            val comment = commentEditText.text.toString().trim()
            if (comment.isNotEmpty()) {
                craftViewModel.writeSuggestion(SuggestionRequest(comment))
                craftViewModel.getCraftMain()
                commentEditText.text.clear()
            } else {
                Toast.makeText(requireContext(), "댓글을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

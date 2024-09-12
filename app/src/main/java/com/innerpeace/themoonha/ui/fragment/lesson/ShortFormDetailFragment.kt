package com.innerpeace.themoonha.ui.fragment.lesson

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.ShortFormAdapter
import com.innerpeace.themoonha.adapter.ShortFormDetailAdapter
import com.innerpeace.themoonha.data.model.lesson.ShortFormDTO
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LessonService
import com.innerpeace.themoonha.data.repository.LessonRepository
import com.innerpeace.themoonha.databinding.FragmentShortFormDetailBinding
import com.innerpeace.themoonha.viewModel.LessonViewModel
import com.innerpeace.themoonha.viewModel.factory.LessonViewModelFactory

class ShortFormDetailFragment : Fragment() {

    private var _binding: FragmentShortFormDetailBinding? = null
    val binding get() = _binding!!
    private lateinit var adapter: ShortFormDetailAdapter
    private val viewModel: LessonViewModel by activityViewModels {
        LessonViewModelFactory(LessonRepository(ApiClient.getClient().create(LessonService::class.java)))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShortFormDetailBinding.inflate(inflater, container, false)
        activity?.findViewById<Toolbar>(R.id.toolbar)?.visibility = View.GONE
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)?.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager = binding.viewPager2

        viewModel.currentShortFormId.value?.let { viewModel.getShortFormDetail(it) }

        viewModel.shortFormList.observe(viewLifecycleOwner) { shortForms ->
            adapter = ShortFormDetailAdapter(shortForms)
            binding.viewPager2.adapter = adapter

            val selectedPosition = arguments?.getInt("selectedPosition") ?: 0
            viewPager.setCurrentItem(selectedPosition, false)

            binding.leftArrow.setOnClickListener {
                findNavController().navigateUp()
            }

            binding.hamburgerMenu.setOnClickListener {
                val popupMenu = PopupMenu(requireContext(), it)
                popupMenu.inflate(R.menu.short_form_menu)

                popupMenu.setOnMenuItemClickListener {
                    val currentShortFormId = viewModel.currentShortFormId.value
                    val bundle = bundleOf("lessonId" to currentShortFormId)
                    findNavController().navigate(R.id.action_shortFormDetailFragment_to_lessonDetailFragment, bundle)
                    true
                }
                popupMenu.show()
            }

        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.currentPage = position
                Log.i("position : ", position.toString())
                val selectedShortForm = adapter.getShortFormAt(position)
                selectedShortForm?.lessonId?.let { lessonId ->
                    viewModel.setCurrentShortFormId(lessonId)
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

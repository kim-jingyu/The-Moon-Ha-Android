package com.innerpeace.themoonha.ui.fragment.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.ShortFormDetailAdapter
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LessonService
import com.innerpeace.themoonha.data.repository.LessonRepository
import com.innerpeace.themoonha.databinding.FragmentShortFormDetailBinding
import com.innerpeace.themoonha.viewModel.LessonViewModel
import com.innerpeace.themoonha.viewModel.factory.LessonViewModelFactory

class ShortFormDetailFragment : Fragment() {

    private var _binding: FragmentShortFormDetailBinding? = null
    private val binding get() = _binding!!
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

        viewModel.shortFormList.observe(viewLifecycleOwner) { shortForms ->
            adapter = ShortFormDetailAdapter(shortForms)
            viewPager.adapter = adapter

            val selectedPosition = arguments?.getInt("selectedPosition") ?: 0
            viewPager.setCurrentItem(selectedPosition, false)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
            }
        })

        binding.leftArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.hamburgerMenu.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.inflate(R.menu.short_form_menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_view_details -> {
                        findNavController().navigate(R.id.action_shortFormDetailFragment_to_lessonDetailFragment)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

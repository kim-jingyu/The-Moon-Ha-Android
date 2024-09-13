package com.innerpeace.themoonha.ui.fragment.beforeafter

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.bite.BeforeAfterAdapter
import com.innerpeace.themoonha.data.repository.BeforeAfterRepository
import com.innerpeace.themoonha.databinding.FragmentBeforeAfterListBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.viewModel.BeforeAfterViewModel
import com.innerpeace.themoonha.viewModel.factory.BeforeAfterViewModelFactory

/**
 * Before&After 프래그먼트
 * @author 김진규
 * @since 2024.08.25
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.25  	김진규       최초 생성
 * </pre>
 */
class BeforeAfterListFragment : Fragment() {
    private var _binding: FragmentBeforeAfterListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BeforeAfterAdapter
    private val viewModel: BeforeAfterViewModel by viewModels {
        BeforeAfterViewModelFactory(BeforeAfterRepository())
    }

    private var sortOption: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBeforeAfterListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.apply {
            showToolbar()
            showBottomNavigation()
        }

        setHasOptionsMenu(true)
        setupToBite()
        setupRecyclerView()
        setupSpinner()
        observeViewModel()
    }

    private fun setupSpinner() {
        val sortOptions = arrayOf("최신순", "제목순")
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.spinner_selected_item, sortOptions)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.sort.adapter = arrayAdapter

        binding.sort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        viewModel.getBeforeAfterList()
                        sortOption = 0
                    }
                    1 -> {
                        viewModel.getBeforeAfterListOrderByTitle()
                        sortOption = 1
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.getBeforeAfterList()
            }
        }
    }

    private fun setupToBite() {
        binding.biteForField.setOnClickListener {
            findNavController().navigate(R.id.fieldListFragment)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        menu.findItem(R.id.item1).isVisible = false
        menu.findItem(R.id.item2).isVisible = false

        if (menu.findItem(Menu.FIRST) == null) {
            menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "").apply {
                setIcon(R.drawable.ic_to_enroll_resized)
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }
            menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "").apply {
                setIcon(R.drawable.ic_to_search_resized)
                setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            Menu.FIRST -> {
                navigateToBeforeAfterEnrollContents()
                true
            }
            Menu.FIRST + 1 -> {
                navigateToSearchFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, 2)
        binding.beforeAfterListRecyclerView.layoutManager = gridLayoutManager

        adapter = BeforeAfterAdapter(emptyList()) { position ->
            navigateToBeforeAfterDetail(position)
        }
        binding.beforeAfterListRecyclerView.adapter = adapter
    }

    private fun navigateToBeforeAfterDetail(selectedPosition: Int) {
        findNavController().navigate(
            R.id.beforeAfterDetailFragment,
            Bundle().apply {
                putInt("selectedPosition", selectedPosition)
                putInt("sortOption", sortOption)
            }
        )
    }

    private fun observeViewModel() {
        viewModel.beforeAfterListResponse.asLiveData().observe(viewLifecycleOwner) { contents ->
            adapter.updateContents(contents)
        }
    }

    private fun navigateToSearchFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, BeforeAfterSearchFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToBeforeAfterEnrollContents() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, BeforeAfterEnrollContentsFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
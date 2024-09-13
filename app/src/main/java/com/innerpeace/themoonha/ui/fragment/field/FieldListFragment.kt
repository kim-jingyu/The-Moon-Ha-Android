package com.innerpeace.themoonha.ui.fragment.field

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.bite.FieldListAdapter
import com.innerpeace.themoonha.data.model.field.FieldCategoryGroup
import com.innerpeace.themoonha.data.model.field.FieldListResponse
import com.innerpeace.themoonha.data.repository.FieldRepository
import com.innerpeace.themoonha.databinding.FragmentFieldListBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import com.innerpeace.themoonha.ui.fragment.beforeafter.BeforeAfterSearchFragment
import com.innerpeace.themoonha.viewModel.FieldViewModel
import com.innerpeace.themoonha.viewModel.factory.FieldViewModelFactory
import kotlinx.coroutines.flow.collect

/**
 * 분야별 한 입 목록 조회 프래그먼트
 * @author 김진규
 * @since 2024.09.05
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.09.05  	김진규       최초 생성
 * </pre>
 */
class FieldListFragment : Fragment() {
    private var _binding: FragmentFieldListBinding? = null
    private val binding get() = _binding!!

    private lateinit var fieldListAdapter: FieldListAdapter
    private val viewModel: FieldViewModel by viewModels {
        FieldViewModelFactory(FieldRepository())
    }

    private var sortOption: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFieldListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.apply {
            setToolbarTitle("문화 한 입")
            showToolbar()
            showBottomNavigation()
        }

        setHasOptionsMenu(true)
        setupRecyclerView()
        setupToBeforeAfter()
        setupSpinner()
        observeFieldList()
    }

    private fun observeFieldList() {
        lifecycleScope.launchWhenStarted {
            viewModel.fieldListResponse.collect { fieldList ->
                if (fieldList.isNotEmpty()) {
                    val groupFieldList = groupDataByCategory(fieldList)
                    fieldListAdapter.update(groupFieldList)
                }
            }
        }
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
                        viewModel.getFieldList()
                        sortOption = 0
                    }
                    1 -> {
                        viewModel.getFieldListOrderByTitle()
                        sortOption = 1
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.getFieldList()
            }
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
                navigateToFieldEnrollContents()
                true
            }
            Menu.FIRST + 1 -> {
                navigateToSearchFragment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToSearchFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, BeforeAfterSearchFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToFieldEnrollContents() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, FieldEnrollContentsFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun groupDataByCategory(fieldList: List<FieldListResponse>): List<FieldCategoryGroup> {
        return fieldList.groupBy { it.categoryId }
            .mapNotNull { (categoryId, fields) ->
                val firstField = fields.firstOrNull()
                firstField?.category?.let { categoryName ->
                    FieldCategoryGroup(
                        category = categoryName.ifEmpty { "카테고리 없음" },
                        fieldList = fields
                    )
                }
            }
    }

    private fun setupRecyclerView() {
        fieldListAdapter = FieldListAdapter(emptyList()) { position ->
            navigateToFieldDetail(position)
        }

        binding.fieldListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fieldListAdapter
            setHasFixedSize(true)
        }
    }
    private fun navigateToFieldDetail(selectedPosition: Int) {
        findNavController().navigate(
            R.id.fieldDetailFragment,
            Bundle().apply {
                putInt("selectedPosition", selectedPosition)
                putInt("sortOption", sortOption)
            }
        )
    }

    private fun setupToBeforeAfter() {
        binding.beforeAfter.setOnClickListener {
            findNavController().navigate(R.id.beforeAfterListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.innerpeace.themoonha.ui.fragment.field

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
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
import com.innerpeace.themoonha.viewmodel.FieldViewModel
import com.innerpeace.themoonha.viewmodel.factory.FieldViewModelFactory
import kotlinx.coroutines.flow.collect

class FieldListFragment : Fragment() {
    private var _binding: FragmentFieldListBinding? = null
    private val binding get() = _binding!!

    private lateinit var fieldListAdapter: FieldListAdapter
    private val viewModel: FieldViewModel by viewModels {
        FieldViewModelFactory(FieldRepository())
    }

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

        lifecycleScope.launchWhenResumed {
            viewModel.fieldListResponse.collect { fieldList ->
                Log.d("FieldListFragment", "Collected field list: ${fieldList.size} items")
                if (fieldList.isEmpty()) {
                    Log.e("FieldListFragment", "fieldList is empty!")
                }
                val groupFieldList = groupDataByCategory(fieldList)
                Log.d("FieldListFragment", "GroupFieldList size: ${groupFieldList.size}")
                fieldListAdapter.update(groupFieldList)
            }
        }
    }

    private fun setupSpinner() {
        val sortOptions = arrayOf("최신순", "제목순")
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
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
                    }
                    1 -> {
                        viewModel.getFieldListOrderByTitle()
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
        Log.d("FieldListFragment", "Grouping data by category, field list size: ${fieldList.size}")
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
        fieldListAdapter = FieldListAdapter(emptyList()) { content ->
            navigateToFieldDetail(content)
        }

        binding.fieldListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fieldListAdapter
            setHasFixedSize(true)
        }
    }

    private fun navigateToFieldDetail(content: FieldListResponse) {
        viewModel.getFieldDetail(content.fieldId)
        viewModel.fieldDetailResponse.asLiveData().observe(viewLifecycleOwner) { detailResponse ->
            if (detailResponse == null) {
                Log.e("FieldListFragment", "Detail response is null!")
                return@observe
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, FieldDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("fieldDetailResponse", detailResponse)
                    }
                })
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupToBeforeAfter() {
        binding.beforeAfter.setOnClickListener {
            findNavController().navigate(R.id.action_field_to_beforeAfterList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
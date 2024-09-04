package com.innerpeace.themoonha.ui.fragment.field

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.adapter.FieldListAdapter
import com.innerpeace.themoonha.data.model.field.FieldCategoryGroup
import com.innerpeace.themoonha.data.model.field.FieldListResponse
import com.innerpeace.themoonha.data.repository.FieldRepository
import com.innerpeace.themoonha.databinding.FragmentFieldListBinding
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
        
        setHasOptionsMenu(true)
        setupRecyclerView()
        setupToBeforeAfter()
        lifecycleScope.launchWhenStarted { 
            viewModel.fieldListResponse.collect { fieldList ->
                val groupFieldList = groupDataByCategory(fieldList)
                fieldListAdapter.update(groupFieldList)
            }
        }
    }

    private fun groupDataByCategory(fieldList: List<FieldListResponse>): List<FieldCategoryGroup> {
        return fieldList.groupBy { it.categoryId }
            .map { (categoryId, fields) ->
                FieldCategoryGroup(
                    categoryName = fields.first().categoryName,
                    fieldList = fields
                )
            }
    }

    private fun setupRecyclerView() {
        fieldListAdapter = FieldListAdapter(emptyList())
        binding.fieldListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = fieldListAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupToBeforeAfter() {
        binding.beforeAfter.setOnClickListener {
            findNavController().navigate(R.id.action_to_beforeAfterList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
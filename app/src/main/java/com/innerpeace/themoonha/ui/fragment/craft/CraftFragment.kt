package com.innerpeace.themoonha.ui.fragment.craft

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.innerpeace.themoonha.adapter.PrologueThemeAdapter
import com.innerpeace.themoonha.data.model.craft.PrologueDTO
import com.innerpeace.themoonha.data.model.craft.PrologueThemeDTO
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCraftBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as? MainActivity)?.setToolbarTitle("문화공방")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.prologueRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        craftViewModel.craftMainResponse.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                val themeList = convertToPrologueThemeDTOList(it.prologueList)
                val adapter = PrologueThemeAdapter(themeList)
                binding.prologueRecyclerView.adapter = adapter
            }
        })

        craftViewModel.getCraftMain()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.innerpeace.themoonha.ui.fragment.live

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.live.LiveLessonDetailResponse
import com.innerpeace.themoonha.data.repository.LiveRepository
import com.innerpeace.themoonha.databinding.FragmentLiveStreamingMainBinding
import com.innerpeace.themoonha.viewmodel.LiveViewModel
import com.innerpeace.themoonha.viewmodel.factory.LiveViewModelFactory

class LiveStreamingMainFragment : Fragment() {
    private var _binding: FragmentLiveStreamingMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LiveViewModel by viewModels {
        LiveViewModelFactory(LiveRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLiveStreamingMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resp: LiveLessonDetailResponse = arguments?.getParcelable("liveLessonDetailResponse")!!
        if (resp == null){
            Log.e("LiveMainFragment", "No detailResponse received in arguments")
            return
        }

        val mainInfoFragment = LiveStreamingMainInfoFragment().apply {
            arguments = Bundle().apply {
                putParcelable("liveLessonDetailResponse", resp)
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.live_streaming_info_container, mainInfoFragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.innerpeace.themoonha.ui.fragment.beforeafter

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.databinding.FragmentBeforeAfterEnrollContentsBinding
import com.innerpeace.themoonha.databinding.FragmentBeforeAfterEnrollContentsPhraseBinding

class BeforeAfterEnrollContentsPhraseFragment : Fragment() {
    private var _binding: FragmentBeforeAfterEnrollContentsPhraseBinding? = null
    private val binding get() = _binding!!

    private var beforeContentUri: Uri? = null
    private var afterContentUri: Uri? = null

    private val hashtags = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            beforeContentUri = it.getParcelable("beforeContentUri")
            afterContentUri = it.getParcelable("afterContentUri")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBeforeAfterEnrollContentsPhraseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayContent()

        binding.inputPhrase.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.endsWith(" ") || text.endsWith("\n")) {
                    val trimmedText = text.trim()
                    if (trimmedText.startsWith("#")) {
                        addHashtag(trimmedText)
                        binding.inputPhrase.text.clear()
                    }
                }
            }
        })

        binding.nextButton.setOnClickListener {
            submitBeforeAfterContent()
        }
    }

    private fun addHashtag(tag: String) {
        hashtags.add(tag)

        val hashtagView = TextView(requireContext()).apply {
            id = View.generateViewId()
            text = tag
            setBackgroundResource(R.drawable.hashtag_background)
            setPadding(16, 8, 16, 8)
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            layoutParams = ConstraintLayout.LayoutParams (
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = 8
                bottomMargin = 8
            }
        }

        binding.root.addView(hashtagView)
        binding.hashtagFlow.referencedIds = binding.hashtagFlow.referencedIds + hashtagView.id
    }

    private fun submitBeforeAfterContent() {
        val phrase = binding.inputPhrase.text.toString()

        val beforeIsImage = beforeContentUri?.let {
            requireContext().contentResolver.getType(it)?.startsWith("image") == true
        } ?: false

        val afterIsImage = afterContentUri?.let {
            requireContext().contentResolver.getType(it)?.startsWith("image") == true
        } ?: false

        val beforeUrl = beforeContentUri.toString()
        val afterUrl = afterContentUri.toString()


    }

    private fun displayContent() {
        beforeContentUri?.let { uri ->
            val mimeType = requireContext().contentResolver.getType(uri)
            if (mimeType?.startsWith("image") == true) {
                binding.beforeContentImage.visibility = View.GONE
                binding.beforeContentVideo.visibility = View.VISIBLE
                binding.beforeContentVideo.setVideoURI(uri)
            } else if (mimeType?.startsWith("video") == true) {
                binding.beforeContentImage.visibility = View.GONE
                binding.beforeContentVideo.visibility = View.VISIBLE
                binding.beforeContentVideo.setVideoURI(uri)
                binding.beforeContentVideo.start()
            }
        }

        afterContentUri?.let { uri ->
            val mimeType = requireContext().contentResolver.getType(uri)
            if (mimeType?.startsWith("image") == true) {
                binding.afterContentImage.visibility = View.VISIBLE
                binding.afterContentVideo.visibility = View.GONE
                binding.afterContentImage.setImageURI(uri)
            } else if (mimeType?.startsWith("video") == true) {
                binding.afterContentImage.visibility = View.GONE
                binding.afterContentVideo.visibility = View.VISIBLE
                binding.afterContentVideo.setVideoURI(uri)
                binding.afterContentVideo.start()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
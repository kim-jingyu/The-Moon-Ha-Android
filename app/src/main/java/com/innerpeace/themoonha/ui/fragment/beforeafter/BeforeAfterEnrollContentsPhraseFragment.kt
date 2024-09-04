package com.innerpeace.themoonha.ui.fragment.beforeafter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.data.model.beforeafter.BeforeAfterRequest
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.LessonService
import com.innerpeace.themoonha.data.repository.BeforeAfterRepository
import com.innerpeace.themoonha.data.repository.LessonRepository
import com.innerpeace.themoonha.databinding.FragmentBeforeAfterEnrollContentsPhraseBinding
import com.innerpeace.themoonha.viewmodel.BeforeAfterViewModel
import com.innerpeace.themoonha.viewmodel.LessonViewModel
import com.innerpeace.themoonha.viewmodel.factory.BeforeAfterViewModelFactory
import com.innerpeace.themoonha.viewmodel.factory.LessonViewModelFactory
import kotlinx.coroutines.flow.collect
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class BeforeAfterEnrollContentsPhraseFragment : Fragment() {
    private var _binding: FragmentBeforeAfterEnrollContentsPhraseBinding? = null
    private val binding get() = _binding!!

    private val beforeAfterViewModel: BeforeAfterViewModel by viewModels {
        BeforeAfterViewModelFactory(BeforeAfterRepository())
    }

    private val lessonViewModel: LessonViewModel by viewModels {
        LessonViewModelFactory(LessonRepository(ApiClient.getClient().create(LessonService::class.java)))
    }

    private var beforeContentUri: Uri? = null
    private var afterContentUri: Uri? = null
    private var selectedLessonId: Long? = null
    private val hashtags = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            beforeContentUri = it.getParcelable("beforeContentUri")
            afterContentUri = it.getParcelable("afterContentUri")
        }

        val memberId = getCurrentMemberId()
        lessonViewModel.getLessonEnroll()
    }

    private fun getCurrentMemberId(): Long? {
        return null
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
        val lessonSpinner = binding.lessonSpinner

        lessonViewModel.lessonEnroll.observe(viewLifecycleOwner, { lessons ->
            val lessonTitles = lessons.map { it.title }
            val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lessonTitles)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            lessonSpinner.adapter = arrayAdapter

            lessonSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    selectedLessonId = lessons[p2].lessonId

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    selectedLessonId = null
                }
            }

            binding.nextButton.setOnClickListener {
                submitBeforeAfterContent()
            }
        })

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
        val lessonId = selectedLessonId ?: run {
            return
        }

        val beforeAfterRequest = BeforeAfterRequest(
            lessonId = lessonId,
            title = binding.inputPhrase.text.toString(),
            hashtags = hashtags
        )

        val beforeContent = beforeContentUri?.let { uri ->
            convertUriToMultiPart(uri, "beforeContent")
        }

        val afterContent = afterContentUri?.let { uri ->
            convertUriToMultiPart(uri, "afterContent")
        }

        val beforeThumbnail = beforeContentUri?.let { uri ->
            createThumbnailMultiPart(uri, "beforeThumbnail")
        }

        val afterThumbnail = afterContentUri?.let { uri ->
            createThumbnailMultiPart(uri, "afterThumbnail")
        }

        if (beforeContent != null && afterContent != null && beforeThumbnail != null && afterThumbnail != null) {
            beforeAfterViewModel.makeBeforeAfter(
                Gson().toJson(beforeAfterRequest).toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()),
                beforeContent,
                afterContent,
                beforeThumbnail,
                afterThumbnail
            )
            observeMakeBeforeAfterResult()
        }
    }

    private fun observeMakeBeforeAfterResult() {
        lifecycleScope.launchWhenStarted {
            beforeAfterViewModel.makeBeforeAfterResponse.collect { result ->
                result?.fold(
                    onSuccess = {
                        findNavController().navigate(R.id.action_to_beforeAfterList)
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "페이지 전환에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    private fun createThumbnailMultiPart(uri: Uri, content: String): MultipartBody.Part {
        val contentResolver = requireContext().contentResolver
        val type = contentResolver.getType(uri)

        val thumbnailFile = if (type?.startsWith("image") == true) {
            createImageThumbnail(uri)
        } else if (type?.startsWith("video") == true) {
            createVideoThumbnail(uri)
        } else {
            null
        }

        return thumbnailFile?.let { file ->
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(content, file.name, requestBody)
        } ?: throw IllegalStateException("썸네일 생성 실패")
    }

    private fun createImageThumbnail(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val thumbnail = Bitmap.createScaledBitmap(bitmap, 150, 150, true)

        val tempFile = File.createTempFile("thumbnail", ".jpg", requireContext().cacheDir)
        val outputStream = FileOutputStream(tempFile)
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        outputStream.flush()
        outputStream.close()

        return tempFile
    }

    private fun createVideoThumbnail(uri: Uri): File {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(requireContext(), uri)

        val bitmap = retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)

        val tempFile = File.createTempFile("thumbnail", ".jpg", requireContext().cacheDir)
        val outputStream = FileOutputStream(tempFile)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        outputStream.flush()
        outputStream.close()

        retriever.release()

        return tempFile
    }


    private fun convertUriToMultiPart(uri: Uri, content: String): MultipartBody.Part {
        val contentResolver = requireContext().contentResolver
        val tempFile = File.createTempFile(content, ".tmp", requireContext().cacheDir)
        tempFile.outputStream().use {
            contentResolver.openInputStream(uri)?.copyTo(it)
        }
        return MultipartBody.Part.createFormData(
            content,
            tempFile.name,
            tempFile.asRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
        )
    }

    private fun displayContent() {
        beforeContentUri?.let { uri ->
            val mimeType = requireContext().contentResolver.getType(uri)
            if (mimeType?.startsWith("image") == true) {
                binding.beforeContentImage.visibility = View.VISIBLE
                binding.beforeContentVideo.visibility = View.GONE
                binding.beforeContentImage.setImageURI(uri)
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
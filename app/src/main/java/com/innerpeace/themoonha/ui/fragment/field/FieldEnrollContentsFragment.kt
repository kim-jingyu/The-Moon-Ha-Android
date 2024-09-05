package com.innerpeace.themoonha.ui.fragment.field

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.databinding.FragmentFieldEnrollContentsBinding
import com.innerpeace.themoonha.ui.activity.common.MainActivity
import java.io.File

class FieldEnrollContentsFragment : Fragment() {
    private var _binding: FragmentFieldEnrollContentsBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_CAMERA_PERMISSION = 100
    private val REQUEST_GALLERY = 1
    private val REQUEST_CAMERA_PHOTO = 2
    private val REQUEST_CAMERA_VIDEO = 3

    private var currentRequestCode: Int = REQUEST_CAMERA_PHOTO
    private var photoUri: Uri? = null

    private var contentUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFieldEnrollContentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.apply {
            hideToolbar()
            hideBottomNavigation()
        }

        binding.backButton.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white))
        binding.backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.cameraButton.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.white))
        binding.cameraButton.setOnClickListener {
            if (hasCameraPermission()) {
                selectCameraMode()
            } else {
                requestCameraPermission()
            }
        }

        binding.gallery.setOnClickListener {
            selectGalleryTarget()
        }

        binding.nextButton.setOnClickListener {
            navigateToNextFragment()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                selectCameraMode()
            } else {
                Toast.makeText(requireContext(), "카메라 및 저장소 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val contentUri = when (requestCode) {
                REQUEST_GALLERY -> data?.data
                REQUEST_CAMERA_PHOTO -> photoUri
                REQUEST_CAMERA_VIDEO -> data?.data
                else -> null
            }

            contentUri?.let { uri ->
                val contentResolver = requireContext().contentResolver
                val mimeType = contentResolver.getType(uri)

                Log.d("mimeType", "mimeType=${mimeType}")

                when {
                    mimeType?.startsWith("image") == true -> {
                        setImageContent(uri)
                    }
                    mimeType?.startsWith("video") == true -> {
                        setVideoContent(uri)
                    }
                }
            }
        }
    }

    private fun setVideoContent(uri: Uri) {
        contentUri = uri
        binding.contentImage.visibility = View.GONE
        binding.contentVideo.visibility = View.VISIBLE
        binding.contentVideo.setVideoURI(uri)
        binding.contentVideo.start()
    }

    private fun setImageContent(uri: Uri) {
        contentUri = uri
        binding.contentImage.visibility = View.VISIBLE
        binding.contentVideo.visibility = View.GONE
        binding.contentImage.setImageURI(uri)
    }

    private fun navigateToNextFragment() {
        val nextFragment = FieldEnrollContentsPhraseFragment()
        nextFragment.arguments = Bundle().apply {
            putParcelable("contentUri", contentUri)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, nextFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun selectGalleryTarget() {
        currentRequestCode = REQUEST_GALLERY
        openGalleryForContent()
    }

    private fun openGalleryForContent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        startActivityForResult(intent, currentRequestCode)
    }

    private fun requestCameraPermission() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
            ),
            REQUEST_CAMERA_PERMISSION
        )
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun selectCameraMode() {
        val actionOptions = arrayOf("사진 촬영", "비디오 녹화")
        val actionBuilder = AlertDialog.Builder(requireContext())
        actionBuilder.setTitle("촬영 방식")
        actionBuilder.setItems(actionOptions) { _, actionOption ->
            currentRequestCode = when (actionOption) {
                0 -> REQUEST_CAMERA_PHOTO
                1 -> REQUEST_CAMERA_VIDEO
                else -> REQUEST_CAMERA_PHOTO
            }
            when (actionOption) {
                0 -> openCameraForPhoto()
                1 -> openCameraForVideo()
            }
        }
        actionBuilder.show()
    }

    private fun openCameraForVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, currentRequestCode)
    }

    private fun openCameraForPhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoUri = createImageFileUri()
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, currentRequestCode)
    }

    private fun createImageFileUri(): Uri {
        val imageFile = File.createTempFile(
            "IMG_${System.currentTimeMillis()}",
            ".jpg",
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
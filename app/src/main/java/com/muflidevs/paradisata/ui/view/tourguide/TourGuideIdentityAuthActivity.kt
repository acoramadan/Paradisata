package com.muflidevs.paradisata.ui.view.tourguide

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityTourGuideIdentityAuthBinding
import com.muflidevs.paradisata.databinding.ActivityTourGuideListBinding
import com.muflidevs.paradisata.databinding.ActivityTouristIdentityAuthBinding
import com.muflidevs.paradisata.ui.view.tourist.TouristIdentityAuthActivity
import com.muflidevs.paradisata.ui.view.tourist.TouristIdentityAuthActivity.Companion
import com.muflidevs.paradisata.viewModel.RegistrationViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TourGuideIdentityAuthActivity : AppCompatActivity() {
    private lateinit var binding:
            ActivityTourGuideIdentityAuthBinding
    private val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private lateinit var viewModel: RegistrationViewModel
    private val timeStamp: String =
        SimpleDateFormat(FILENAME_FORMAT, Locale.US).
        format(Date())
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding =
            ActivityTourGuideIdentityAuthBinding
                .inflate(layoutInflater)

        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)

        viewModel.imageUri.observe(this) {uri ->
            uri.let {
                currentImageUri = it
                showImage()
            }
        }
        with(binding) {
            imageKtp.setOnClickListener {
                showMediaOptionDialog()
            }

        }
    }
    private fun showMediaOptionDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Media Source")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> startCamera()
                1 -> startGallery()
            }
        }
        builder.show()
    }

    private fun startGallery() {
        galleryResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val galleryResultLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setImageUri(uri)
        } else {
            Log.d("TouristIdentityAuthActivity", "No media selected")
        }
    }

    private fun startCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    TouristIdentityAuthActivity.CAMERA_PERMISSION_REQUEST_CODE
                )
                return
            }
        }
        val newImageUri = createImageUriForCamera(this)
        currentImageUri = newImageUri
        viewModel.setImageUri(currentImageUri!!)
        cameraResultLauncher.launch(currentImageUri!!)
    }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                currentImageUri?.let { uri ->
                    viewModel.setImageUri(uri)
                    displayImage(uri)
                }
            } else {
                currentImageUri = null
                viewModel.setImageUri(null)
            }
        }

    private fun showImage() {
        viewModel.imageUri.value.let { uri ->
            try {
                binding.imageKtp.setImageURI(null)
                binding.imageKtp.setImageURI(uri)
            } catch (e: Exception) {
                Log.e("AddActivity", "gagal menampilkan gambar ${e.message}")
            }

        }
    }

    private fun createImageUriForCamera(context: Context): Uri {
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
            }
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        }
        return uri ?: createImageUriForLegacyDevices(context)
    }

    private fun createImageUriForLegacyDevices(context: Context): Uri {
        val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(fileDir, "/MyCamera/$timeStamp.jpg")
        if (imageFile.parentFile?.exists() == false) {
            imageFile.parentFile?.mkdirs()
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    private fun displayImage(uri: Uri) {
        try {
            binding.profileImage.setImageURI(uri)
        } catch (e: Exception) {
            Log.e("TouristIdentityAuthActivity", "Failed to display image: ${e.message}")
        }
    }
    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }
}
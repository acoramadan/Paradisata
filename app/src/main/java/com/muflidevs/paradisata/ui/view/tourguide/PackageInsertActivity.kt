package com.muflidevs.paradisata.ui.view.tourguide

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.registration.PackageName
import com.muflidevs.paradisata.databinding.ActivityPackageInsertBinding
import com.muflidevs.paradisata.ui.view.RegisterSuccessActivity
import com.muflidevs.paradisata.ui.view.tourist.TouristIdentityAuthActivity.Companion.CAMERA_PERMISSION_REQUEST_CODE
import com.muflidevs.paradisata.viewModel.RegistrationViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PackageInsertActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPackageInsertBinding
    private lateinit var viewModel: RegistrationViewModel
    private var totalGuest = 0
    private val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    private var currentImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPackageInsertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnMore.setOnClickListener { totalGuest++ }
            btnLess.setOnClickListener {
                if (totalGuest < 0) totalGuest = 0
                else totalGuest--
            }
            imageHomestay.setOnClickListener {
                showMediaOptionDialog()
            }
            btnSubmit.setOnClickListener {
                regsiter()
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
                    CAMERA_PERMISSION_REQUEST_CODE
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
                binding.imageHomestay.setImageURI(null)
                binding.imageHomestay.setImageURI(uri)
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
            binding.imageHomestay.setImageURI(uri)
        } catch (e: Exception) {
            Log.e("TouristIdentityAuthActivity", "Failed to display image: ${e.message}")
        }
    }

    private fun regsiter() {
        try {
            with(binding) {
                val facilitiesString = edtTxtFacilities.text.toString()
                val facilitiesList = facilitiesString.split(",").map { it.trim() }.toMutableList()

                val uuid = intent.getStringExtra("extra_uuid")
                val packageName = PackageName(
                    id = UUID.randomUUID().toString(),
                    name = edtTxtPackageInformation.text.toString(),
                    homeStayPhoto = currentImageUri.toString(),
                    address = edtTxtAddress.text.toString(),
                    facilities = facilitiesList,
                    transportationPhoto = " ",
                    transportationType = " ",
                    totalGuest = totalGuest
                )

                startActivity(
                    Intent(
                        this@PackageInsertActivity,
                        ActivityTransportationType::class.java
                    ).apply {
                        putExtra("extra_package", packageName)
                        putExtra("extra_id", uuid)
                    }
                )
                Log.d("PackageInsertActivity","Data yang dikirim : ${packageName} \n uuid : $uuid")

            }
        } catch (e: Exception) {
            Log.e("TourisIdentityAuthActivity", "${e.message}")
        }
    }
}
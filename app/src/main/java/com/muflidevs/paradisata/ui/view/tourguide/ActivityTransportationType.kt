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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.registration.PackageName
import com.muflidevs.paradisata.databinding.ActivityTransportationTypeBinding
import com.muflidevs.paradisata.ui.view.ProsesRegisterActivity
import com.muflidevs.paradisata.ui.view.tourist.TouristIdentityAuthActivity.Companion.CAMERA_PERMISSION_REQUEST_CODE
import com.muflidevs.paradisata.viewModel.RegistrationViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ActivityTransportationType : AppCompatActivity() {
    private lateinit var binding: ActivityTransportationTypeBinding
    private lateinit var viewModel: RegistrationViewModel
    private val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    private var currentImageUri: Uri? = null
    private var vehicleType = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTransportationTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)
            .get(RegistrationViewModel::class.java)

        viewModel.imageUri.observe(this) {
            it.let {
                currentImageUri = it
                showImage()
            }
        }
        with(binding) {
            imageTransportation.setOnClickListener {
                showMediaOptionDialog()
            }
            fourWheelsBtn.setOnClickListener {
                fourWheelsBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@ActivityTransportationType,
                        R.color.md_theme_secondaryContainer
                    )
                )
                twoWheelsBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@ActivityTransportationType,
                        R.color.md_theme_onSecondary
                    )
                )
                vehicleType = "Car"
            }
            twoWheelsBtn.setOnClickListener {
                twoWheelsBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@ActivityTransportationType,
                        R.color.md_theme_secondaryContainer
                    )
                )
                fourWheelsBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@ActivityTransportationType,
                        R.color.md_theme_onSecondary
                    )
                )
                vehicleType = "Bike"
            }
            btnSubmitTourist.setOnClickListener {
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
                binding.imageTransportation.setImageURI(null)
                binding.imageTransportation.setImageURI(uri)
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
            binding.imageTransportation.setImageURI(uri)
        } catch (e: Exception) {
            Log.e("TouristIdentityAuthActivity", "Failed to display image: ${e.message}")
        }
    }

    private fun regsiter() {
        try {
            viewModel = RegistrationViewModel(application)
            val uuid = intent.getStringExtra("extra_id")
            val uuid2 = intent.getStringExtra("extra_id_2")
            val intentPackage = intent.getParcelableExtra<PackageName>("extra_package")!!
            val packageName = PackageName(
                id = intentPackage.id,
                name = intentPackage.name,
                homeStayPhoto = intentPackage.homeStayPhoto,
                address = intentPackage.address,
                facilities = intentPackage.facilities,
                transportationPhoto = currentImageUri.toString(),
                transportationType = vehicleType,
                totalGuest = intentPackage.totalGuest
            )
            lifecycleScope.launch {
                viewModel.registerPackageTourguide(packageName, uuid ?: "", uuid2, packageName.id)
                startActivity(
                    Intent(
                        this@ActivityTransportationType,
                        ProsesRegisterActivity::class.java
                    )
                )
                Log.d("PackageInsertActivity", "Data yang dikirim : ${packageName} \n uuid : $uuid")
            }
        } catch (e: Exception) {
            Log.e("TourisIdentityAuthActivity", "${e.message}")
        }
    }
}
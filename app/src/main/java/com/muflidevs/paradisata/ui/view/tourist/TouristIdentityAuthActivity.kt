package com.muflidevs.paradisata.ui.view.tourist

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.storage.FirebaseStorage
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.Tourist
import com.muflidevs.paradisata.databinding.ActivityTouristIdentityAuthBinding
import com.muflidevs.paradisata.ui.view.FailedRegisterActivity
import com.muflidevs.paradisata.ui.view.RegisterSuccessActivity
import com.muflidevs.paradisata.viewModel.RegistrationViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Suppress("DEPRECATION")
class TouristIdentityAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTouristIdentityAuthBinding
    private lateinit var viewModel: RegistrationViewModel
    private lateinit var gender: String
    private lateinit var touristFrom: String
    private val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTouristIdentityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)

        binding.profileImage.setOnClickListener {
            showMediaOptionDialog()
        }
        viewModel.imageUri.observe(this) { uri ->
            uri.let {
                currentImageUri = it
                showImage()
            }
        }
        binding.apply {
            maleBtn.setOnClickListener {
                maleBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TouristIdentityAuthActivity,
                        R.color.md_theme_secondaryContainer
                    )
                )
                femaleBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TouristIdentityAuthActivity,
                        R.color.md_theme_onSecondary
                    )
                )
                gender = "Male"
            }
            femaleBtn.setOnClickListener {
                maleBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TouristIdentityAuthActivity,
                        R.color.md_theme_onSecondary
                    )
                )
                femaleBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TouristIdentityAuthActivity,
                        R.color.md_theme_secondaryContainer
                    )
                )
                gender = "female"
            }

            foreignBtn.setOnClickListener {
                foreignBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TouristIdentityAuthActivity,
                        R.color.md_theme_secondaryContainer
                    )
                )
                domesticBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TouristIdentityAuthActivity,
                        R.color.md_theme_onSecondary
                    )
                )
                touristFrom = "Foreign"
            }
            domesticBtn.setOnClickListener {
                foreignBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TouristIdentityAuthActivity,
                        R.color.md_theme_onSecondary
                    )
                )
                domesticBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TouristIdentityAuthActivity,
                        R.color.md_theme_secondaryContainer
                    )
                )
                touristFrom = "domestic"
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
                binding.profileImage.setImageURI(null)
                binding.profileImage.setImageURI(uri)
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

    private fun regsiter() {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("image/${UUID.randomUUID()}.jpg")
        val uploadTask = imageRef.putFile(currentImageUri!!)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                try {
                    with(binding) {
                        val uuid = intent.getStringExtra("extra_uuid")
                        Log.d("TouristIdentityAuthActivity", "UUID yg diterima: $uuid")
                        val tourist =
                            com.muflidevs.paradisata.data.model.remote.registration.Tourist(
                                id = UUID.randomUUID().toString(),
                                fullName = edtTxtFullname.text.toString(),
                                address = edtTxtAddress.text.toString(),
                                gender = gender,
                                touristFrom = touristFrom,
                                photo = downloadUrl
                            )
                        lifecycleScope.launch {
                            viewModel = RegistrationViewModel(application)
                            try {
                                viewModel.registerTourist(tourist, uuid ?: " ",tourist.id)
                                Toast.makeText(
                                    this@TouristIdentityAuthActivity,
                                    "Berhasil Registrasi",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(
                                    Intent(
                                        this@TouristIdentityAuthActivity,
                                        RegisterSuccessActivity::class.java
                                    )
                                )
                                finish()
                            } catch (e: Exception) {
                                startActivity(
                                    Intent(
                                        this@TouristIdentityAuthActivity,
                                        FailedRegisterActivity::class.java
                                    )
                                )
                                Log.e("TourisIdentityAuthActivity", "${e.message}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("TourisIdentityAuthActivity", "${e.message}")
                }
            }
        }
    }

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }
}

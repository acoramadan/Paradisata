package com.muflidevs.paradisata.ui.view.tourguide

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.storage.FirebaseStorage
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.registration.TourGuide
import com.muflidevs.paradisata.databinding.ActivityTourGuideIdentityAuthBinding
import com.muflidevs.paradisata.viewModel.RegistrationViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TourGuideIdentityAuthActivity : AppCompatActivity() {
    private lateinit var binding:
            ActivityTourGuideIdentityAuthBinding
    private val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private lateinit var viewModel: RegistrationViewModel
    private val timeStamp: String =
        SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())
    private var currentImageUri: Uri? = null
    private lateinit var gender: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding =
            ActivityTourGuideIdentityAuthBinding
                .inflate(layoutInflater)

        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]

        viewModel.imageUri.observe(this) { uri ->
            uri.let {
                currentImageUri = it
                showImage()
            }
        }
        with(binding) {
            imageKtp.setOnClickListener {
                showMediaOptionDialog()
            }
            maleBtn.setOnClickListener {
                maleBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TourGuideIdentityAuthActivity,
                        R.color.md_theme_secondaryContainer
                    )
                )
                femaleBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TourGuideIdentityAuthActivity,
                        R.color.md_theme_onSecondary
                    )
                )
                gender = "Male"
            }
            femaleBtn.setOnClickListener {
                maleBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TourGuideIdentityAuthActivity,
                        R.color.md_theme_onSecondary
                    )
                )
                femaleBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@TourGuideIdentityAuthActivity,
                        R.color.md_theme_secondaryContainer
                    )
                )
                gender = "female"
            }
            edtTxtDatebirth.setOnClickListener {
                val calendar = java.util.Calendar.getInstance()

                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    this@TourGuideIdentityAuthActivity,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                        binding.edtTxtDatebirth.setText(date)
                    },
                    year, month, day
                )

                datePickerDialog.show()
            }
            btnSubmitTourist.setOnClickListener {
                register()
            }
        }
    }

    private fun showMediaOptionDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Media Source")
        builder.setItems(options) { _, which ->
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

    @SuppressLint("ObsoleteSdkInt")
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
            binding.imageKtp.setImageURI(uri)
        } catch (e: Exception) {
            Log.e("TouristIdentityAuthActivity", "Failed to display image: ${e.message}")
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun register() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("image/${UUID.randomUUID()}.jpg")
        val uploadTask = imageRef.putFile(currentImageUri!!)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                with(binding) {
                    val tourGuide =
                        dateFormat.parse(binding.edtTxtDatebirth.text.toString())?.let {
                            TourGuide(
                                id = UUID.randomUUID().toString(),
                                fullName = edtTxtFullname.text.toString(),
                                address = edtTxtAddress.text.toString(),
                                gender = gender,
                                dateOfBirth = it.toString(),
                                photo = downloadUrl
                            )
                        }
                    val uuid = intent.getStringExtra("extra_uuid")
                    try {
                        lifecycleScope.launch {
                            viewModel = RegistrationViewModel(application)

                            viewModel.registerTourguide(
                                tourGuide = tourGuide!!,
                                uuid ?: "",
                                tourGuide.id
                            )
                            Toast.makeText(
                                this@TourGuideIdentityAuthActivity,
                                "Berhasil Registrasi",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(
                                    this@TourGuideIdentityAuthActivity,
                                    PackageInsertActivity::class.java
                                ).apply {
                                    putExtra("extra_uuid_1", uuid)
                                    Log.d("TourGuideIdentity", "UUID1 : $uuid")
                                    putExtra("extra_uuid_2", tourGuide.id)
                                    Log.d("TourGuideIdentitiy   ", "UUID2 :${tourGuide.id}")
                                }
                            )
                            finish()
                        }
                    } catch (e: Exception) {
                        Log.d("register Tour Guide", e.message!!)
                    }
                }
            }
        }
    }

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }
}
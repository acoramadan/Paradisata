package com.muflidevs.paradisata.ui.view

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.databinding.ActivityOrderBinding
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var tourGuide: TourGuide
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tourGuide = intent.getParcelableExtra("tourguide")!!
        with(binding) {
            Glide.with(this@OrderActivity)
                .load(tourGuide.homestay)
                .placeholder(R.drawable.placeholder)
                .into(imageBackground)

            Glide.with(this@OrderActivity)
                .load(tourGuide.profilePicture)
                .placeholder(R.drawable.placeholder)
                .into(profileImage)

            tourGuideName.text = tourGuide.name
        }
        binding.nextBtn.setOnClickListener {
            startActivity(Intent(this@OrderActivity, DestinationsOrderActivity::class.java).apply {
                val date = getDateRange()
                val note = binding.etSidenote.text.toString()
                Log.d("OrderActivity", "rentang tanggal yang dikirim : $date")
                putExtra(EXTRA_DATE, date)
                putExtra(EXTRA_NOTE, note)

            })
        }

        binding.etCheckin.setOnClickListener {
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this, { _, selectedYear, selectedMonth, selectedDay ->
                    val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.etCheckin.setText(date)
                },
                year, month, day
            )

            datePickerDialog.show()
        }

        binding.etCheckout.setOnClickListener {
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this, { _, selectedYear, selectedMonth, selectedDay ->
                    val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.etCheckout.setText(date)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateRange(): Int {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        return try {
            val checkinDate = dateFormat.parse(binding.etCheckin.text.toString())
            val checkoutDate = dateFormat.parse(binding.etCheckout.text.toString())

            val diffInMillis = (checkoutDate?.time ?: 0) - (checkinDate?.time ?: 0)
            TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    companion object {
        const val EXTRA_DATE = "extra_date"
        const val EXTRA_NOTE = "extra_note"
    }
}
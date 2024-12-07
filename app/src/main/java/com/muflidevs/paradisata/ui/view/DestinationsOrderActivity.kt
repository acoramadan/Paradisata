package com.muflidevs.paradisata.ui.view

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityDestinationsOrderBinding

class DestinationsOrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDestinationsOrderBinding
    private var count = 0
    private var dateRange = 0
    private val placesSelected = mutableListOf<String>()
    private var totalPrices = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDestinationsOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dateRange = intent.getIntExtra(OrderActivity.EXTRA_DATE, 0)

        binding.btnAdd.setOnClickListener {
            if (count < dateRange) {
                val intent = Intent(this@DestinationsOrderActivity, ListDestinationActivity::class.java)
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                binding.btnAdd.visibility = View.GONE
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedPlace = data?.getStringExtra(ListDestinationActivity.EXTRA_SELECTED_PLACE)
            val pricePlace = data?.getIntExtra(ListDestinationActivity.EXTRA_PRICE,0)
            Log.d("SelectedPlace", "$selectedPlace")

           pricePlace.let {
               totalPrices += it ?: 0
               binding.price.text = "Rp.$totalPrices K"
           }
            selectedPlace?.let {
                placesSelected.add(it)
                count++

                binding.listOrder.text = placesSelected.joinToString(", ")

                if (count >= dateRange) {
                    binding.btnAdd.visibility = View.GONE
                }
            }
        }
    }
    companion object {
        const val REQUEST_CODE = 1001
    }
}

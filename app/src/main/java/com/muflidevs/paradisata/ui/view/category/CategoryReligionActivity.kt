package com.muflidevs.paradisata.ui.view.category

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.databinding.ActivityCategoryHistoryBinding
import com.muflidevs.paradisata.ui.view.DetailActivity
import com.muflidevs.paradisata.ui.view.adapter.ImageSliderAdapter
import com.muflidevs.paradisata.ui.view.adapter.ReligiListAdapter
import com.muflidevs.paradisata.viewModel.PlaceViewModel

class CategoryReligionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryHistoryBinding
    private lateinit var adapter: ReligiListAdapter
    private lateinit var viewModel: PlaceViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val images = listOf(
            R.drawable.dummy_religi,
            R.drawable.dummy_religi2,
            R.drawable.dummy_religi3
        )

        val viewPager = binding.viewPager
        viewPager.adapter = ImageSliderAdapter(images)
        viewModel = PlaceViewModel(application)

        autoSlide(viewPager, images.size)
        setupRecycleView(binding)

        viewModel.loadPlaces(0)
        viewModel.places.observe(this) { dataPlace ->
            adapter.submitList(dataPlace)
        }
        binding.exitButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecycleView(binding: ActivityCategoryHistoryBinding) {
        adapter = ReligiListAdapter(this) { dataPlaces ->
            onCategoryClicked(dataPlaces)
        }
        binding.apply {
            rvHistory.adapter = adapter
            rvHistory.layoutManager = LinearLayoutManager(this@CategoryReligionActivity)
            rvHistory.isNestedScrollingEnabled = false
        }
    }

    private fun onCategoryClicked(dataPlace: DataPlaces) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("Item", dataPlace)
        }
        startActivity(intent)
    }

    private fun autoSlide(viewPager: ViewPager2, itemCount: Int) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            var currentItem = 0
            override fun run() {
                currentItem = (currentItem + 1) % itemCount
                viewPager.setCurrentItem(currentItem, true)
                handler.postDelayed(this, 3000)
            }
        }
        handler.post(runnable)
    }
}
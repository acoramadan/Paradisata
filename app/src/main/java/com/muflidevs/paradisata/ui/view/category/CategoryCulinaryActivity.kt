package com.muflidevs.paradisata.ui.view.category

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.databinding.ActivityCategoryCulinaryBinding
import com.muflidevs.paradisata.ui.view.adapter.CulinaryListAdapter
import com.muflidevs.paradisata.ui.view.adapter.ImageSliderAdapter
import com.muflidevs.paradisata.viewModel.PlaceViewModel

class CategoryCulinaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryCulinaryBinding
    private lateinit var adapter: CulinaryListAdapter
    private lateinit var viewModel: PlaceViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryCulinaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val images = listOf(
            R.drawable.dummy_kuliner,
            R.drawable.dummy_kuliner2,
            R.drawable.dummy_kuliner3,
            R.drawable.dummy_kuliner4,
            R.drawable.dummy_kuliner5
        )
        val viewPager = binding.viewPager
        viewPager.adapter = ImageSliderAdapter(images)
        viewModel = PlaceViewModel(application)

        autoSlide(viewPager, images.size)
        setupRecycleView(binding)

        viewModel.loadPlaces(0)
        viewModel.places.observe(this) {dataPlace ->
            adapter.submitList(dataPlace)
        }
    }

    private fun setupRecycleView(binding: ActivityCategoryCulinaryBinding) {
        adapter = CulinaryListAdapter(this) {dataPlaces ->
            onCategoryClicked(dataPlaces)
        }
        binding.apply {
            rvCulinary.adapter = adapter
            rvCulinary.layoutManager = LinearLayoutManager(this@CategoryCulinaryActivity)
            rvCulinary.isNestedScrollingEnabled = false
        }
    }
    private fun onCategoryClicked(dataPlace: DataPlaces) {

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
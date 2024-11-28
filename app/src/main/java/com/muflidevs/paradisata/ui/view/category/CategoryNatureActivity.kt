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
import com.muflidevs.paradisata.databinding.ActivityCategoryNatureBinding
import com.muflidevs.paradisata.ui.view.DetailActivity
import com.muflidevs.paradisata.ui.view.adapter.ImageSliderAdapter
import com.muflidevs.paradisata.ui.view.adapter.NatureListAdapter
import com.muflidevs.paradisata.viewModel.PlaceViewModel

class CategoryNatureActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryNatureBinding
    private lateinit var adapter: NatureListAdapter
    private lateinit var viewModel: PlaceViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryNatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val images = listOf(
            R.drawable.dummy_alam,
            R.drawable.dummy_alam2,
            R.drawable.dummy_alam3,
            R.drawable.dummy_alam4,
            R.drawable.dummy_alam5,
            R.drawable.dummy_alam6
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

    private fun setupRecycleView(binding: ActivityCategoryNatureBinding) {
        adapter = NatureListAdapter(this) { dataPlaces ->
            onCategoryClicked(dataPlaces)
        }
        binding.apply {
            rvNature.adapter = adapter
            rvNature.layoutManager = LinearLayoutManager(this@CategoryNatureActivity)
            rvNature.isNestedScrollingEnabled = false
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
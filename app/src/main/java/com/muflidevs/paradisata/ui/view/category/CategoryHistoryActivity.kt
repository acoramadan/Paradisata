package com.muflidevs.paradisata.ui.view.category

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.databinding.ActivityCategoryHistoryBinding
import com.muflidevs.paradisata.ui.view.DetailActivity
import com.muflidevs.paradisata.ui.view.adapter.HistoryListAdapter
import com.muflidevs.paradisata.ui.view.adapter.ImageSliderAdapter
import com.muflidevs.paradisata.viewModel.PlaceViewModel

class CategoryHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCategoryHistoryBinding
    private lateinit var adapter: HistoryListAdapter
    private lateinit var viewModel: PlaceViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val images = listOf(
            R.drawable.dummy_sejarah,
            R.drawable.dummy_sejarah2,
            R.drawable.dummy_sejarah3,
            R.drawable.dummy_sejarah4,
            R.drawable.dummy_sejarah5
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
        viewModel.isLoading.observe(this) {
            setProgressBar(it)
        }
        binding.exitButton.setOnClickListener {
            finish()
        }
    }

    private fun setupRecycleView(binding: ActivityCategoryHistoryBinding) {
        adapter = HistoryListAdapter(this) { dataPlaces ->
            onCategoryClicked(dataPlaces)
        }
        binding.apply {
            rvHistory.adapter = adapter
            rvHistory.layoutManager = LinearLayoutManager(this@CategoryHistoryActivity)
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
    private fun setProgressBar(isLoading: Boolean) {
        binding.progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
    }
}
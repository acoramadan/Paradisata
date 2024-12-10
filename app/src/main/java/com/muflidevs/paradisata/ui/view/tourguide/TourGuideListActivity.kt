package com.muflidevs.paradisata.ui.view.tourguide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.databinding.ActivityTourGuideListBinding
import com.muflidevs.paradisata.ui.view.adapter.HomeVerticalGridAdapter
import com.muflidevs.paradisata.ui.view.adapter.TourGuideListAdapter
import com.muflidevs.paradisata.viewModel.TourGuideViewModel
import com.muflidevs.paradisata.viewModel.factory.TourGuideViewModelFactory

class TourGuideListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTourGuideListBinding
    private lateinit var adapter: HomeVerticalGridAdapter
    private val viewModel: TourGuideViewModel by viewModels {
        TourGuideViewModelFactory(application)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTourGuideListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecycleView()
        viewModel.fetchTourGuide()
        viewModel.tourGuide.observe(this) { tourGuide ->
            adapter.submitList(tourGuide)
        }
        viewModel.isLoading.observe(this) {
            setProgressBar(it)
        }
    }

    private fun setupRecycleView() {
        val prices = intent.getIntExtra("price", 0)

        adapter = HomeVerticalGridAdapter(this) { tourGuide ->
            Log.d("price", "data yang dikirim : $tourGuide + $prices")
            onCategoryClicked(tourGuide, prices)
        }

        binding.apply {
            recylerView.layoutManager = GridLayoutManager(this@TourGuideListActivity, 2)
            recylerView.adapter = adapter
        }
    }

    private fun onCategoryClicked(tourGuide: TourGuide, price: Int) {
        val intent = Intent(this@TourGuideListActivity, TourGuideDetailActivity::class.java).apply {
            putExtra("tourGuide", tourGuide)
            putExtra("price", price)
        }
        Log.d("TourGuideListActivity", "Data yang dikirim : $tourGuide, $price")
        startActivity(intent)

    }

    private fun setProgressBar(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
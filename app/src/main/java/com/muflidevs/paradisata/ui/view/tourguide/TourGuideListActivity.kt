package com.muflidevs.paradisata.ui.view.tourguide

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.databinding.ActivityTourGuideListBinding
import com.muflidevs.paradisata.ui.view.adapter.TourGuideListAdapter
import com.muflidevs.paradisata.viewModel.TourGuideViewModel

class TourGuideListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTourGuideListBinding
    private lateinit var adapter: TourGuideListAdapter
    private lateinit var viewModel: TourGuideViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTourGuideListBinding.inflate(layoutInflater)
        viewModel = TourGuideViewModel(application)
        setContentView(binding.root)
        adapter = TourGuideListAdapter(this) { tourGuide ->
            Log.d("TourguideListActivity", "data yang dikirim : $tourGuide")
            onCategoryClicked(tourGuide)
        }

        binding.apply {
            recylerView.layoutManager = LinearLayoutManager(this@TourGuideListActivity)
            recylerView.adapter = adapter
        }

        viewModel.tourGuide.observe(this) { tourGuide ->
            adapter.submitList(tourGuide)
        }
        viewModel.isLoading.observe(this) {
            setProgressBar(it)
        }
        viewModel.fetchTourGuide()
    }


    private fun onCategoryClicked(tourGuide: TourGuide) {
        val intent = Intent(this@TourGuideListActivity,TourGuideDetailActivity::class.java).apply {
            intent.putExtra("item",tourGuide)
        }
        startActivity(intent)

    }
    private fun setProgressBar(isLoading: Boolean) {
        binding.progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
    }
}
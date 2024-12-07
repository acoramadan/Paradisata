package com.muflidevs.paradisata.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.databinding.ActivityListDestinationBinding
import com.muflidevs.paradisata.ui.view.adapter.HomeHorizontalAdapter
import com.muflidevs.paradisata.ui.view.adapter.HomeVerticalGridAdapter
import com.muflidevs.paradisata.viewModel.PlaceViewModel

class ListDestinationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListDestinationBinding
    private lateinit var viewModel: PlaceViewModel
    private lateinit var adapterGrid: HomeHorizontalAdapter
    private var total = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListDestinationBinding.inflate(layoutInflater)

        setContentView(binding.root)
        viewModel = PlaceViewModel(application)
        viewModel.loadPlaces(0)
        viewModel.places.observe(this) { dataPlaces ->
            adapterGrid.submitList(dataPlaces)
        }

        setupRecycleView()
    }

    private fun setupRecycleView() {

        adapterGrid = HomeHorizontalAdapter(this@ListDestinationActivity) { dataPlaces ->
            onCategoryItemClicked(dataPlaces)
        }
        binding.listPlaces.apply {
            layoutManager =
                GridLayoutManager(this@ListDestinationActivity, 2)
            adapter = this@ListDestinationActivity.adapterGrid
        }
    }

    private fun onCategoryItemClicked(dataPlaces: DataPlaces) {
        val intent = Intent()
        intent.putExtra(EXTRA_SELECTED_PLACE,dataPlaces.nama)
        intent.putExtra(EXTRA_PRICE,parseAmount(dataPlaces.harga))
        setResult(RESULT_OK,intent)

        finish()
    }

    private fun parseAmount(amount: String?) : Int {
        val numberString = amount?.replace(Regex("[^\\d]"), "")
        return numberString?.toIntOrNull() ?: 0
    }

    companion object {
        const val EXTRA_SELECTED_PLACE = "selected_place"
        const val EXTRA_PRICE = "price"
    }
}
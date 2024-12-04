package com.muflidevs.paradisata.ui.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager

import androidx.recyclerview.widget.LinearLayoutManager
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.databinding.FragmentHomeBinding
import com.muflidevs.paradisata.ml.TfLiteModel
import com.muflidevs.paradisata.ui.view.DetailActivity
import com.muflidevs.paradisata.ui.view.adapter.CategoryListAdapter
import com.muflidevs.paradisata.ui.view.adapter.HomeHorizontalAdapter
import com.muflidevs.paradisata.ui.view.adapter.HomeVerticalGridAdapter
import com.muflidevs.paradisata.ui.view.category.CategoryCulinaryActivity
import com.muflidevs.paradisata.ui.view.category.CategoryHistoryActivity
import com.muflidevs.paradisata.ui.view.category.CategoryNatureActivity
import com.muflidevs.paradisata.ui.view.category.CategoryReligionActivity
import com.muflidevs.paradisata.ui.view.tourguide.TourGuideDetailActivity
import com.muflidevs.paradisata.viewModel.PlaceViewModel
import com.muflidevs.paradisata.viewModel.TourGuideViewModel

class HomeFragment : Fragment() {

    private val viewModel: PlaceViewModel by viewModels()
    private val tourGuideModel: TourGuideViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterHorizontal: HomeHorizontalAdapter
    private lateinit var adapterGrid: HomeVerticalGridAdapter
    private var userSelectedCategory: String = "Wisata Alam"
    private lateinit var model: TfLiteModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cobaMl()
        setupRecyleView()
        viewModel.loadPlaces()
        viewModel.places.observe(viewLifecycleOwner) { places ->
            adapterHorizontal.submitList(places)
        }
        tourGuideModel.fetchTourGuide()
        tourGuideModel.tourGuide.observe(viewLifecycleOwner) { tourGuide ->
            adapterGrid.submitList(tourGuide)
        }
        binding.tesBtn.setOnClickListener{
            cobaMl()
        }
        iconBarClick()
    }

    private fun iconBarClick() {
        with(binding) {
            icNature.setOnClickListener {
                val intent = Intent(requireContext(), CategoryNatureActivity::class.java)
                startActivity(intent)
            }
            icHistory.setOnClickListener {
                val intent = Intent(requireContext(), CategoryHistoryActivity::class.java)
                startActivity(intent)
            }
            icReligion.setOnClickListener {
                val intent = Intent(requireContext(), CategoryReligionActivity::class.java)
                startActivity(intent)
            }
            icCulinary.setOnClickListener {
                val intent = Intent(requireContext(), CategoryCulinaryActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun onCategoryItemClicked(dataPlaces: DataPlaces) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra("Item", dataPlaces)
        }
        startActivity(intent)
    }

    private fun onCategoryItemClicked(tourGuide: TourGuide) {
        val intent = Intent(requireContext(), TourGuideDetailActivity::class.java).apply {
            putExtra("tourGuide", tourGuide)
        }
        startActivity(intent)
    }

    private fun setupRecyleView() {
        adapterHorizontal = HomeHorizontalAdapter(requireContext()) { dataPlaces ->
            onCategoryItemClicked(dataPlaces)
        }
        adapterGrid = HomeVerticalGridAdapter(requireContext()) { tourGuide ->
            onCategoryItemClicked(tourGuide)
        }
        binding.categoryRv.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = this@HomeFragment.adapterHorizontal
        }
        binding.tourGuideRecomendation.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@HomeFragment.adapterGrid
        }
    }

    private fun cobaMl() {
        model = TfLiteModel(
            modelName = "recommendation_model.tflite",
            context = requireContext(),
            onResult = { result ->
                // Menampilkan hasil prediksi destinasi
                Log.d("HomeFragment",result)
                Toast.makeText(context, "Destinasi yang disarankan: $result", Toast.LENGTH_LONG).show()
            },
            onError = { error ->
                // Menangani error jika terjadi
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
            }
        )
        val activityMap = mapOf(
            "Hiking" to 1,
            "Fotografi" to 2,
            "Menikmati pemandangan" to 3,
            // Tambahkan aktivitas lain jika perlu
        )

        val selectedActivity = "Hiking"  // Aktivitas yang dipilih pengguna (dapat dari UI)
        val rating = 4.7f  // Rating yang diberikan pengguna (dapat dari UI)

        // Mengonversi aktivitas ke format numerik (menggunakan label encoding)
        val activityInput = activityMap[selectedActivity] ?: 0  // Jika tidak ada, default 0

        // Menyiapkan input array untuk model (berisi aktivitas, rating, dan fitur lain jika perlu)
        val inputArray = FloatArray(4)  // Sesuaikan dengan jumlah fitur yang digunakan oleh model
        inputArray[0] = activityInput.toFloat()  // Aktivitas yang sudah di-encode
        inputArray[1] = rating  // Rating yang diberikan pengguna

        // Jika model Anda membutuhkan fitur lain (misalnya kategori), tambahkan di sini
        inputArray[2] = 0f  // Placeholder untuk fitur lain, misalnya kategori
        inputArray[3] = 0f  // Placeholder untuk fitur lain, sesuaikan dengan data model Anda

        // Memanggil model untuk prediksi
        model.predict(inputArray.toList())  // Model akan
    }

}
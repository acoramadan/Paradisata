package com.muflidevs.paradisata.ui.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.db.UserInteraction
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.databinding.FragmentHomeBinding
import com.muflidevs.paradisata.ml.TfLiteModel
import com.muflidevs.paradisata.ui.view.DetailActivity
import com.muflidevs.paradisata.ui.view.adapter.HomeHorizontalAdapter
import com.muflidevs.paradisata.ui.view.adapter.HomeVerticalGridAdapter
import com.muflidevs.paradisata.ui.view.category.CategoryCulinaryActivity
import com.muflidevs.paradisata.ui.view.category.CategoryHistoryActivity
import com.muflidevs.paradisata.ui.view.category.CategoryNatureActivity
import com.muflidevs.paradisata.ui.view.category.CategoryReligionActivity
import com.muflidevs.paradisata.ui.view.tourguide.TourGuideDetailActivity
import com.muflidevs.paradisata.viewModel.DbViewModel
import com.muflidevs.paradisata.viewModel.PlaceViewModel
import com.muflidevs.paradisata.viewModel.TourGuideViewModel
import com.muflidevs.paradisata.viewModel.TouristViewModel
import com.muflidevs.paradisata.viewModel.UserViewModel
import com.muflidevs.paradisata.viewModel.factory.TourGuideViewModelFactory

class HomeFragment : Fragment() {
    private val touristModel: TouristViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val viewModel: PlaceViewModel by viewModels()
    private val tourGuideModel: TourGuideViewModel by viewModels {
        TourGuideViewModelFactory(requireActivity().application)
    }
    private val dbViewModel: DbViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterHorizontal: HomeHorizontalAdapter
    private lateinit var adapterGrid: HomeVerticalGridAdapter
    private var listRecommendation: List<String> = emptyList()
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

        dbViewModel.getAllUserInteractions().observe(viewLifecycleOwner) { userInteractions ->
            recomendationFetchMl(userInteractions)
            viewModel.loadRecommendationsPlaces(data = listRecommendation)
            viewModel.places.observe(viewLifecycleOwner) { places ->
                Log.e("AdapterHorizontal", "$places")
                adapterHorizontal.submitList(places)
            }
        }
        setupRecyleView()
        tourGuideModel.fetchTourGuide()
        tourGuideModel.tourGuide.observe(viewLifecycleOwner) { tourGuide ->
            adapterGrid.submitList(tourGuide)
        }
        userViewModel.getUser(userViewModel.getUserToken())
        userViewModel.user.observe(viewLifecycleOwner) {user ->
           binding.userName.text = user?.userName ?: " "
        }
        touristModel.getTourist(userViewModel.getUserToken())
        touristModel.tourist.observe(viewLifecycleOwner) { tourist ->
            Log.d("HomeFragment" , "Tourist : ${tourist?.photo}")
            Glide.with(requireActivity())
                .load(tourist?.photo)
                .placeholder(R.drawable.placeholder)
                .into(binding.profileImage)
        }
        Log.d("Home Fragment ", " user token : ${userViewModel.getUserToken()}")
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
        Log.d("HomeFragment", "Data yang dikirimkan $tourGuide")
        startActivity(intent)
    }

    private fun setupRecyleView() {
        adapterHorizontal = HomeHorizontalAdapter(requireContext()) { dataPlaces ->
            onCategoryItemClicked(dataPlaces)
        }
        adapterGrid = HomeVerticalGridAdapter(requireContext()) { tourGuide ->
            onCategoryItemClicked(tourGuide)
        }
        binding.rvVisited.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = this@HomeFragment.adapterHorizontal
        }
        binding.rvGuide.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@HomeFragment.adapterGrid
        }
    }

    private fun recomendationFetchMl(userInteractions: List<UserInteraction>) {
        model = TfLiteModel(
            modelName = "recommendation_model.tflite",
            context = requireContext(),
            onResult = { result ->
            },
            onError = { error ->
                Log.d("HomeFragment", error)
            }
        )
        val data = viewModel.readPlacesFromRawString()
        val allCategories = userInteractions.map { it.kategori }.distinct()
        val allActivities = userInteractions.flatMap { it.activites }.distinct()

        val kategoriMap = allCategories
            .mapIndexed { index, category -> category to index + 1 }
            .toMap()

        val activityMap = allActivities
            .mapIndexed { index, activity -> activity to index + 1 }
            .toMap()

        userInteractions.forEach { interaction ->
            val rating = interaction.rating
            val selectedCategory = interaction.kategori

            val categoryInput =
                kategoriMap[selectedCategory] ?: 1

            val activityInputs = interaction.activites.map { activity ->
                val activityInput = activityMap[activity] ?: 0
                activityInput
            }

            val inputArray =
                FloatArray(activityInputs.size + 2)

            activityInputs.forEachIndexed { index, activityInput ->
                inputArray[index] = activityInput.toFloat()
            }

            inputArray[activityInputs.size] = rating
            inputArray[activityInputs.size + 1] = categoryInput.toFloat()

            listRecommendation =
                model.predict(inputArray.toList(), data)

        }
    }


}
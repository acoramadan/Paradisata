package com.muflidevs.paradisata.ui.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.data.model.remote.json.TouristRating
import com.muflidevs.paradisata.databinding.FragmentReviewBinding
import com.muflidevs.paradisata.ui.view.adapter.ReviewListTourGuideAdapter


@Suppress("DEPRECATION")
class TourGuideFragmentReview : Fragment() {
    private lateinit var reviews: List<TouristRating>
    private lateinit var dataPlaces: TourGuide
    private lateinit var binding: FragmentReviewBinding
    private lateinit var adapter: ReviewListTourGuideAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewBinding.inflate(inflater,container,false)
        dataPlaces = arguments?.getParcelable("dataReviewTourGuide")!!
        setupRecyleView()

        return binding.root
    }

    private fun setupRecyleView() {
        adapter = ReviewListTourGuideAdapter(requireContext()) { dataPlaces ->
            onCategoryItemClicked(dataPlaces)
        }
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@TourGuideFragmentReview.adapter
        }
        adapter.submitList(reviews)
    }

    private fun onCategoryItemClicked(dataPlaces: DataPlaces) {

    }
}
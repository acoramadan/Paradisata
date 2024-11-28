package com.muflidevs.paradisata.ui.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.data.model.remote.json.UlasanPlaces
import com.muflidevs.paradisata.databinding.FragmentReviewBinding
import com.muflidevs.paradisata.ui.view.adapter.CategoryListAdapter
import com.muflidevs.paradisata.ui.view.adapter.ReviewListAdapter


@Suppress("DEPRECATION")
class FragmentReview : Fragment() {
    private lateinit var reviews: List<UlasanPlaces>
    private lateinit var dataPlaces: DataPlaces
    private lateinit var binding: FragmentReviewBinding
    private lateinit var adapter: ReviewListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewBinding.inflate(inflater,container,false)
        dataPlaces = arguments?.getParcelable("detailPlace")!!
        reviews = dataPlaces.ulasanList
        setupRecyleView()

        return binding.root
    }

    private fun setupRecyleView() {
        adapter = ReviewListAdapter(requireContext()) { dataPlaces ->
            onCategoryItemClicked(dataPlaces)
        }
        binding.rvReviews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FragmentReview.adapter
        }
        adapter.submitList(reviews)
    }

    private fun onCategoryItemClicked(dataPlaces: DataPlaces) {

    }
}
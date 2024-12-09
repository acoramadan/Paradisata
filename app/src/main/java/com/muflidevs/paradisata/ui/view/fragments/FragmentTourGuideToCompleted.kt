package com.muflidevs.paradisata.ui.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.databinding.FragmentTourGuideToAcceptBinding
import com.muflidevs.paradisata.databinding.FragmentTourGuideToCompletedBinding
import com.muflidevs.paradisata.ui.view.adapter.BookingItemAdapter
import com.muflidevs.paradisata.viewModel.TourGuideViewModel


class FragmentTourGuideToCompleted : Fragment() {
    private lateinit var binding: FragmentTourGuideToCompletedBinding
    private lateinit var viewModel: TourGuideViewModel
    private lateinit var adapter: BookingItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.let { TourGuideViewModel(it.application) }!!
        viewModel.fetchTourGuide(1)
        adapter = BookingItemAdapter(requireContext()) { tourGuide ->
            onCategoryClicked(tourGuide)
        }
        viewModel.tourGuide.observe(viewLifecycleOwner) { tourGuide ->
            adapter.submitList(tourGuide)
        }
        binding.apply {
            recylerView.layoutManager = LinearLayoutManager(requireContext())
            recylerView.adapter = adapter
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTourGuideToCompletedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun onCategoryClicked(tourGuide: TourGuide) {

    }

}
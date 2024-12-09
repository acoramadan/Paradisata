package com.muflidevs.paradisata.ui.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.databinding.FragmentTourGuideBinding
import com.muflidevs.paradisata.ui.view.adapter.TourGuideListAdapter
import com.muflidevs.paradisata.ui.view.tourguide.TourGuideDetailActivity
import com.muflidevs.paradisata.viewModel.TourGuideViewModel


class TourGuideFragment : Fragment() {
    private lateinit var binding: FragmentTourGuideBinding
    private lateinit var viewModel: TourGuideViewModel
    private lateinit var adapter: TourGuideListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { TourGuideViewModel(it.application) }!!
        adapter = TourGuideListAdapter(requireContext()) { tourGuide ->
            onCategoryClicked(tourGuide)
        }
        viewModel.fetchTourGuide(1)
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
    ): View {
        binding = FragmentTourGuideBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun onCategoryClicked(tourGuide: TourGuide) {
        startActivity(Intent(requireContext(),TourGuideDetailActivity::class.java).apply {
            putExtra("tourGuide",tourGuide)
        })
    }
}
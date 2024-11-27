package com.muflidevs.paradisata.ui.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.databinding.FragmentHomeBinding
import com.muflidevs.paradisata.ui.view.adapter.CategoryListAdapter
import com.muflidevs.paradisata.ui.view.category.CategoryCulinaryActivity
import com.muflidevs.paradisata.ui.view.category.CategoryHistoryActivity
import com.muflidevs.paradisata.ui.view.category.CategoryNatureActivity
import com.muflidevs.paradisata.ui.view.category.CategoryReligionActivity
import com.muflidevs.paradisata.viewModel.PlaceViewModel

class HomeFragment : Fragment() {

    private val viewModel: PlaceViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyleView()
        viewModel.loadPlaces()
        viewModel.places.observe(viewLifecycleOwner) { places ->
            adapter.submitList(places.take(4))
        }

    }

    private fun onCategoryItemClicked(dataPlaces: DataPlaces) {
        when (dataPlaces.kategori) {
            "Wisata Alam" -> {
                val intent = Intent(requireContext(), CategoryNatureActivity::class.java)
                startActivity(intent)
            }

            "Wisata Religi" -> {
                val intent = Intent(requireContext(), CategoryReligionActivity::class.java)
                startActivity(intent)
            }

            "Wisata Kuliner" -> {
                val intent = Intent(requireContext(), CategoryCulinaryActivity::class.java)
                startActivity(intent)
            }

            "Wisata Sejarah" -> {
                val intent = Intent(requireContext(), CategoryHistoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupRecyleView() {
        adapter = CategoryListAdapter(requireContext()) { dataPlaces ->
            onCategoryItemClicked(dataPlaces)
        }
        binding.categoryRv.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@HomeFragment.adapter
        }
    }
}
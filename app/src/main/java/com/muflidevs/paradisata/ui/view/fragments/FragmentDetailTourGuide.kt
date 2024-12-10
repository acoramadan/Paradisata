package com.muflidevs.paradisata.ui.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.databinding.FragmentDetailTourGuideBinding

@Suppress("DEPRECATION")
class FragmentDetailTourGuide : Fragment() {
    private lateinit var binding: FragmentDetailTourGuideBinding
    private lateinit var tourGuide: TourGuide

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailTourGuideBinding.inflate(layoutInflater,container,false)
        tourGuide = arguments?.getParcelable("dataTourGuide")!!

        with(binding) {
            tvNameTourGuide.text = tourGuide.name
            tourGuideDescription.text = tourGuide.about
            tvDetailVehicle.text = tourGuide.transportationType
            Glide.with(requireActivity())
                .load(tourGuide.profilePicture)
                .into(profileImage)
            Glide.with(requireActivity())
                .load(tourGuide.transportationPicture)
                .into(transportationPicture)
            populateDynamicList(tourGuide.facilities,tvFacilities)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun populateDynamicList(dataList: List<String>?, textView: TextView) {
        if (dataList.isNullOrEmpty()) {
            textView.text = ""
        } else {

            val formattedText = dataList.joinToString(separator = "\n• ") { it }
            textView.text = "• $formattedText"
        }
    }

}
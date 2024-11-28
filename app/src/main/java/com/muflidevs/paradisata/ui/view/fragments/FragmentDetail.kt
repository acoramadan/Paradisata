package com.muflidevs.paradisata.ui.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.databinding.FragmentDetailBinding

class FragmentDetail : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private var places: DataPlaces? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        places = arguments?.getParcelable("detailPlace")

        places?.let {
            with(binding) {
                tentangText.text = it.tentang

                populateDynamicList(it.fasilitas, fasilitas1, fasilitas2, fasilitas3, fasilitas4)

                populateDynamicList(it.aktivitas, aktivitas1, aktivitas2, aktivitas3)

                harga.text = it.harga
                jamBukaText.text = it.jamBuka
            }
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun populateDynamicList(dataList: List<String>?, vararg textViews: TextView) {
        if (dataList.isNullOrEmpty()) {
            textViews.forEachIndexed { index, textView ->
                textView.text = ""
            }
        } else {
            dataList.forEachIndexed { index, item ->
                if (index < textViews.size) {
                    textViews[index].text = "• $item"
                }
            }

            for (i in dataList.size until textViews.size) {
                textViews[i].text = ""
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(item: DataPlaces) = FragmentDetail().apply {
            arguments = Bundle().apply {
                putParcelable("detailPlace", item)
            }
        }
    }
}

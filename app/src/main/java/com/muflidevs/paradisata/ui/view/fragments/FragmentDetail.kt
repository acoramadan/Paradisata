package com.muflidevs.paradisata.ui.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.db.UserInteraction
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.databinding.FragmentDetailBinding
import com.muflidevs.paradisata.viewModel.DbViewModel

class FragmentDetail : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private var places: DataPlaces? = null
    private lateinit var userInteraction: UserInteraction
    private lateinit var viewModel: DbViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(inflater, container, false)

        places = arguments?.getParcelable("detailPlace")
        viewModel = DbViewModel(requireActivity().application!!)

        places?.let {
            with(binding) {
                tentangText.text = it.tentang
                userInteraction =
                    UserInteraction(
                        activites = listOf(
                            aktivitas1.text.toString(),
                            aktivitas2.text.toString(),
                            aktivitas3.text.toString()
                        ),
                        kategori = it.kategori,
                        rating = it.rating
                    )
                populateDynamicList(it.fasilitas, fasilitas1, fasilitas2, fasilitas3, fasilitas4)

                populateDynamicList(it.aktivitas, aktivitas1, aktivitas2, aktivitas3)

                harga.text = it.harga
                jamBukaText.text = it.jamBuka
            }
        }
        saveToDb(places!!)
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

    private fun saveToDb(dataPlaces: DataPlaces) {
        val activites: List<String> =
            with(binding) {
                listOf(
                    aktivitas1.text.toString().drop(1),
                    aktivitas2.text.toString().drop(1),
                    aktivitas3.text.toString().drop(1)
                )
            }
        val category = dataPlaces.kategori
        val rating = dataPlaces.rating

        userInteraction.apply {
            this.activites = activites
            this.kategori = category
            this.rating = rating
        }
        viewModel.insert(userInteraction)
        Log.d("fragment detail", "Data yang di input di database : $userInteraction")
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

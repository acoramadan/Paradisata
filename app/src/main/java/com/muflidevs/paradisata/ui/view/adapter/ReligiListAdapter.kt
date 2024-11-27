package com.muflidevs.paradisata.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.databinding.ItemAdapterPlacesBinding

class ReligiListAdapter(
    private val context: Context,
    private val onItemClicked: (DataPlaces) -> Unit
) :
    ListAdapter<DataPlaces, ReligiListAdapter.ReligiViewHolder>(DIFF_CALLBACK) {

    inner class ReligiViewHolder(private var binding: ItemAdapterPlacesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dataPlaces: DataPlaces, onItemClicked: (DataPlaces) -> Unit) {
            if (dataPlaces.kategori == "Wisata Religi") {
                with(binding) {
                    placesName.text = dataPlaces.nama
                    ratingBar.rating = dataPlaces.rating
                    ratingText.text = dataPlaces.rating.toString()
                    Glide.with(context)
                        .load(dataPlaces.foto[0])
                        .placeholder(R.drawable.placeholder)
                        .into(imageBackground)
                    root.setOnClickListener {
                        onItemClicked(dataPlaces)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReligiViewHolder {
        val binding =
            ItemAdapterPlacesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReligiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReligiViewHolder, position: Int) {
        val detailCulinary = getItem(position)
        if (detailCulinary.kategori == "Wisata Religi") {
            holder.bind(detailCulinary, onItemClicked)
        } else {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataPlaces>() {
            override fun areItemsTheSame(oldItem: DataPlaces, newItem: DataPlaces): Boolean {
                return oldItem.nama == newItem.nama
            }

            override fun areContentsTheSame(oldItem: DataPlaces, newItem: DataPlaces): Boolean {
                return oldItem == newItem
            }

        }
    }
}
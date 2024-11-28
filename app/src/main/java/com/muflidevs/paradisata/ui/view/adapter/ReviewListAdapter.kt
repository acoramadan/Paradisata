package com.muflidevs.paradisata.ui.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.data.model.remote.json.UlasanPlaces
import com.muflidevs.paradisata.databinding.ItemAdapterPlacesBinding
import com.muflidevs.paradisata.databinding.ItemAdapterReviewBinding
import com.muflidevs.paradisata.ui.view.fragments.FragmentReview

class  ReviewListAdapter(
    private val context: Context,
    private val onItemClicked: (DataPlaces) -> Unit
) :
    ListAdapter<UlasanPlaces, ReviewListAdapter.ReviewViewHolder>(DIFF_CALLBACK) {

    inner class ReviewViewHolder(private var binding: ItemAdapterReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ulasanPlaces: UlasanPlaces, onItemClicked: (DataPlaces) -> Unit) {
            with(binding) {
                Log.e("ReviewListAdapter","$ulasanPlaces")
                userName.text = ulasanPlaces.user
                reviews.text = ulasanPlaces.komentar
                ratingBar.rating = ulasanPlaces.rating
                ratingText.text = ulasanPlaces.rating.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding =
            ItemAdapterReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val detailCulinary = getItem(position)
        holder.bind(detailCulinary, onItemClicked)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UlasanPlaces>() {
            override fun areItemsTheSame(oldItem: UlasanPlaces, newItem: UlasanPlaces): Boolean {
                return oldItem.user == newItem.user
            }

            override fun areContentsTheSame(oldItem: UlasanPlaces, newItem: UlasanPlaces): Boolean {
                return oldItem == newItem
            }
        }
    }
}
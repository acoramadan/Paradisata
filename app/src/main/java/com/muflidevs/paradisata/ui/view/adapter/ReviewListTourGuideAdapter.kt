package com.muflidevs.paradisata.ui.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.data.model.remote.json.TouristRating
import com.muflidevs.paradisata.databinding.ItemAdapterReviewBinding

class  ReviewListTourGuideAdapter(
    private val context: Context,
    private val onItemClicked: (DataPlaces) -> Unit
) :
    ListAdapter<TouristRating, ReviewListTourGuideAdapter.ReviewViewListHolder>(DIFF_CALLBACK) {

    inner class ReviewViewListHolder(private var binding: ItemAdapterReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(TouristRating: TouristRating, onItemClicked: (DataPlaces) -> Unit) {
            with(binding) {
                Log.e("ReviewListAdapter","$TouristRating")
                userName.text = TouristRating.idUser.toString()
                reviews.text = TouristRating.ulasan
                ratingBar.rating = TouristRating.rating
                ratingText.text = TouristRating.rating.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewListHolder {
        val binding =
            ItemAdapterReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewListHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewListHolder, position: Int) {
        val detailCulinary = getItem(position)
        holder.bind(detailCulinary, onItemClicked)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TouristRating>() {
            override fun areItemsTheSame(oldItem: TouristRating, newItem: TouristRating): Boolean {
                return oldItem.idUser == newItem.idUser
            }

            override fun areContentsTheSame(oldItem: TouristRating, newItem: TouristRating): Boolean {
                return oldItem == newItem
            }
        }
    }
}
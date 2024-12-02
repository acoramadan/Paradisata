package com.muflidevs.paradisata.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.databinding.ItemAdapterTourGuideBinding

class TourGuideListAdapter(
    private val context: Context,
    private val onItemClicked: (TourGuide) -> Unit
) :
    ListAdapter<TourGuide, TourGuideListAdapter.TourGuideViewHolder>(DIFF_CALLBACK) {

    inner class TourGuideViewHolder(private var binding: ItemAdapterTourGuideBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tourGuide: TourGuide, onItemClicked: (TourGuide) -> Unit) {
            with(binding) {
                tourGuideName.text = tourGuide.name
                ratingText.text = tourGuide.rating.toString()
                Glide.with(context)
                    .load(tourGuide.profilePicture)
                    .placeholder(R.drawable.placeholder)
                    .into(profileImage)
                Glide.with(context)
                    .load(tourGuide.homestay)
                    .placeholder(R.drawable.placeholder)
                    .into(imageBackground)

                setRatingStars(tourGuide.rating)
                root.setOnClickListener {
                    onItemClicked(tourGuide)
                }
            }
        }

        private fun setRatingStars(rating: Float) {
            val stars =
                listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)

            for (i in stars.indices) {
                if (i < rating.toInt()) {
                    stars[i].setImageResource(R.drawable.stars)
                } else {
                    stars[i].setImageResource(R.drawable.stars_kosong)
                }
            }
            if (rating % 1 > 0) {
                stars[rating.toInt()].setImageResource(R.drawable.stars_setengah)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourGuideViewHolder {
        val binding =
            ItemAdapterTourGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TourGuideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TourGuideViewHolder, position: Int) {
        val detailTourGuide = getItem(position)
        holder.bind(detailTourGuide, onItemClicked)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TourGuide>() {
            override fun areItemsTheSame(oldItem: TourGuide, newItem: TourGuide): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TourGuide, newItem: TourGuide): Boolean {
                return oldItem == newItem
            }

        }
    }
}
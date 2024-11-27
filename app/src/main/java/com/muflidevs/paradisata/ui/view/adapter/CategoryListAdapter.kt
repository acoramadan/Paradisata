package com.muflidevs.paradisata.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.databinding.ItemAdapterCategoryBinding

class CategoryListAdapter(
    private val context: Context,
    private val onItemClicked: (DataPlaces) -> Unit
) :
    ListAdapter<DataPlaces, CategoryListAdapter.CategoryViewHolder>(DIFF_CALLBACK) {

    inner class CategoryViewHolder(private var binding: ItemAdapterCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dataPlaces: DataPlaces, onItemClicked: (DataPlaces) -> Unit) {
            with(binding) {
                tvName.text = dataPlaces.kategori
                Glide.with(context)
                    .load(dataPlaces.foto[0])
                    .placeholder(R.drawable.placeholder)
                    .into(imageView)
                root.setOnClickListener {
                    onItemClicked(dataPlaces)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemAdapterCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val detailCategory = getItem(position)
        holder.bind(detailCategory, onItemClicked)
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
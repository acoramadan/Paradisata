package com.muflidevs.paradisata.ui.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.databinding.ActivityDetailBinding
import com.muflidevs.paradisata.ui.view.fragments.FragmentDetail
import com.muflidevs.paradisata.ui.view.fragments.FragmentReview

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val data: DataPlaces? = intent.getParcelableExtra("Item")
        with(binding) {
            detailName.text = data?.nama
            detailLocation.text = data?.alamat
            detailRating.text = data?.rating.toString()
            Glide.with(this@DetailActivity)
                .load(data?.foto?.get(0))
                .placeholder(R.drawable.placeholder)
                .into(imageDetail)
        }
        buttonClick(data)

    }

    private fun buttonClick(data: DataPlaces?) {
        val detailFragment = FragmentDetail().apply {
            arguments = Bundle().apply {
                putParcelable("detailPlace", data)
            }
        }
        val reviewFragment = FragmentReview().apply {
            arguments = Bundle().apply {
                putParcelable("detailPlace", data)
            }
        }
        val fragmentManager = supportFragmentManager

        fragmentManager.beginTransaction()
            .replace(
                binding.frameContainer.id,
                detailFragment,
                FragmentDetail::class.java.simpleName
            )
            .commit()

        with(binding) {
            btnDetail.setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(
                        binding.frameContainer.id,
                        detailFragment,
                        FragmentDetail::class.java.simpleName
                    )
                    .commit()
            }
            btnReviews.setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(
                        binding.frameContainer.id,
                        reviewFragment,
                        FragmentReview::class.java.simpleName
                    )
                    .commit()
            }
            exitButton.setOnClickListener {
                finish()
            }
        }

    }

}
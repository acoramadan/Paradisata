package com.muflidevs.paradisata.ui.view.tourguide

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.databinding.ActivityTourGuideDetailBinding
import com.muflidevs.paradisata.ui.view.fragments.FragmentDetailTourGuide
import com.muflidevs.paradisata.ui.view.fragments.FragmentReview
import com.muflidevs.paradisata.ui.view.fragments.TourGuideFragmentReview

@Suppress("DEPRECATION")
class TourGuideDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTourGuideDetailBinding
    private lateinit var detailTourGuide: TourGuide

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTourGuideDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val prices = intent.getIntExtra("price", 0)
        Log.d(
            "TourGuideDetailActivity",
            "Data yang diterima : ${
                intent.getParcelableExtra<TourGuide>("tourGuide") ?: intent.getParcelableExtra(
                    "tourGuideKe2"
                )!!
            }"
        )
        detailTourGuide = intent.getParcelableExtra("tourGuide")
            ?: intent.getParcelableExtra("tourGuideKe2")!!

        Log.d("TourGuide", "$detailTourGuide + $prices")
        Log.d("prices", "$prices")
        with(binding) {
            detailName.text = "${detailTourGuide.name} Home Stay"
            detailLocation.text = detailTourGuide.address
            detailRating.text = detailTourGuide.rating.toString()
            price.text = "Rp.${(detailTourGuide.prize + prices)}K/night"
            Glide.with(application)
                .load(detailTourGuide.homestay)
                .placeholder(R.drawable.placeholder)
                .into(imageDetail)
        }
        setFragment(detailTourGuide)
    }

    @SuppressLint("CommitTransaction")
    private fun setFragment(data: TourGuide) {
        val fragmentDetail = FragmentDetailTourGuide().apply {
            arguments = Bundle().apply {
                putParcelable("dataTourGuide", data)
            }
        }
        val fragmentReview = TourGuideFragmentReview().apply {
            arguments = Bundle().apply {
                putParcelable("dataReviewTourGuide", data)
            }
        }
        val fragmentManager = supportFragmentManager

        fragmentManager.beginTransaction().replace(
            binding.frameContainer.id,
            fragmentDetail,
            FragmentDetailTourGuide::class.java.simpleName
        ).commit()

        with(binding) {
            btnDetail.setOnClickListener {
                fragmentManager.beginTransaction().replace(
                    binding.frameContainer.id,
                    fragmentDetail,
                    FragmentDetailTourGuide::class.java.simpleName
                ).commit()
            }
            btnReviews.setOnClickListener {
                fragmentManager.beginTransaction().replace(
                    binding.frameContainer.id,
                    fragmentReview,
                    TourGuideFragmentReview::class.java.simpleName
                ).commit()
            }

            exitButton.setOnClickListener {
                finish()
            }
        }
    }
}
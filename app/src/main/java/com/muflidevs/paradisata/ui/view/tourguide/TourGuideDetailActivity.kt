package com.muflidevs.paradisata.ui.view.tourguide

import android.annotation.SuppressLint
import android.os.Bundle
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

@Suppress("DEPRECATION")
class TourGuideDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTourGuideDetailBinding
    private lateinit var detailTourGuide: TourGuide

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTourGuideDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        detailTourGuide = intent.getParcelableExtra<TourGuide>("item")!!

        with(binding) {
            detailName.text = "${detailTourGuide.name} Home Stay"
            detailLocation.text = detailTourGuide.address
            detailRating.text = detailTourGuide.rating.toString()
            Glide.with(application)
                .load(detailTourGuide.homestay)
                .placeholder(R.drawable.placeholder)
                .into(imageDetail)
        }
    }

    @SuppressLint("CommitTransaction")
    private fun setFragment(data: TourGuide) {
        val fragmentDetail = FragmentDetailTourGuide().apply {
           arguments = Bundle().apply {
               putParcelable("dataTourGuide",data)
           }
        }
        val fragmentReview = FragmentReview().apply {
            arguments = Bundle().apply {
                putParcelable("dataReviewTourGuide", data)
            }
        }
        val fragmentManager = supportFragmentManager

        fragmentManager.beginTransaction().replace(
            binding.frameContainer.id,
            fragmentDetail,
            FragmentDetailTourGuide::class.java.simpleName
        )

        with(binding) {
            btnDetail.setOnClickListener {
                fragmentManager.beginTransaction().replace(
                    binding.frameContainer.id,
                    fragmentDetail,
                    FragmentDetailTourGuide::class.java.simpleName
                )
            }
            btnReviews.setOnClickListener {
                fragmentManager.beginTransaction().replace(
                    binding.frameContainer.id,
                    fragmentReview,
                    FragmentReview::class.java.simpleName
                )
            }
            bookingBtn.setOnClickListener {
                
            }
            exitButton.setOnClickListener{
                finish()
            }
        }
    }
}
package com.muflidevs.paradisata.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityRegisterBinding
import com.muflidevs.paradisata.ui.view.tourguide.TourGuideRegisterActivity
import com.muflidevs.paradisata.ui.view.tourist.TouristRegisterActivity

class RegisterActivity : AppCompatActivity(),View.OnClickListener {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var touristBtn: Button
    private lateinit var tourGuideBtn: Button
    private lateinit var loginTv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        touristBtn = binding.touristBtn
        tourGuideBtn = binding.tourGuideBtn
        loginTv = binding.tvLogin

        //navigation
        touristBtn.setOnClickListener(this)
        tourGuideBtn.setOnClickListener(this)
        loginTv.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when(view?.id) {

            R.id.tv_login -> {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.touristBtn -> {
                val intent = Intent(this@RegisterActivity, TouristRegisterActivity::class.java)
                startActivity(intent)
            }

            R.id.tourGuideBtn -> {
                val intent = Intent(this@RegisterActivity, TourGuideRegisterActivity::class.java)
                startActivity(intent)
            }

        }
    }
}
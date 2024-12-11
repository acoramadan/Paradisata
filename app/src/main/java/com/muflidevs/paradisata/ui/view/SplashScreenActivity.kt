package com.muflidevs.paradisata.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.muflidevs.paradisata.data.model.remote.registration.TourGuide
import com.muflidevs.paradisata.databinding.ActivitySplashScreenBinding
import com.muflidevs.paradisata.ui.view.tourguide.TourGuideMainActivity
import com.muflidevs.paradisata.viewModel.UserViewModel


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: UserViewModel by viewModels()
    private var role = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val userToken = viewModel.getUserToken()
        viewModel.checkUserRole(userToken)
        viewModel.userRole.observe(this) { roleUser ->
            role = roleUser
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (userToken.isNotBlank()) {

                Log.d("SplashScreen", "Token = $role")
                when {
                    role == "tourGuide" -> {
                        startActivity(
                            Intent(
                                this@SplashScreenActivity,
                                TourGuideMainActivity::class.java
                            )
                        )
                    }

                    role == "tourist" -> {
                        startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
                    }

                    else -> {
                        startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
                    }
                }
            } else {
                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            }
            finish()
        }, 5000)

    }
}
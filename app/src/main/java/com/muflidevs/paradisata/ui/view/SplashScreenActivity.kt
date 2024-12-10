package com.muflidevs.paradisata.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.muflidevs.paradisata.databinding.ActivitySplashScreenBinding
import com.muflidevs.paradisata.viewModel.UserViewModel


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel : UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            if(viewModel.getUserToken().isNotEmpty()) {
                startActivity(
                    Intent(
                        this@SplashScreenActivity,
                        MainActivity::class.java
                    )
                )
            } else {
                val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()

        }, 5000)
    }
}
package com.muflidevs.paradisata.ui.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.muflidevs.paradisata.databinding.ActivitySplashScreenBinding
import com.muflidevs.paradisata.ui.view.tourist.TouristIdentityAuthActivity
import com.muflidevs.paradisata.ui.view.tourist.TouristRegisterActivity

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreenActivity, OrderActivity::class.java)
            startActivity(intent)
            finish()

//            if (firebaseUser == null) {
//                val intent = Intent(this@SplashScreenActivity, RegisterActivity::class.java)
//                startActivity(intent)
//                finish()
//            } else {
//                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
//            }
        }, 5000)
    }
}
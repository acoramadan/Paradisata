package com.muflidevs.paradisata.ui.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityRegisterSuccessBinding

class RegisterSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterSuccessBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView7.setOnClickListener {
            startActivity(
                Intent(
                    this@RegisterSuccessActivity,
                    LoginActivity::class.java
                )
            )

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(
                    Intent(
                        this@RegisterSuccessActivity,
                        LoginActivity::class.java
                    )
                )
            },5000)
        }
    }
}
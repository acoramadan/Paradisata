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
import com.muflidevs.paradisata.databinding.ActivityFailedRegisterBinding

class FailedRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFailedRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFailedRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textView7.setOnClickListener {
            startActivity(
                Intent(
                    this@FailedRegisterActivity,
                    LoginActivity::class.java
                )
            )

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(
                    Intent(
                        this@FailedRegisterActivity,
                        LoginActivity::class.java
                    )
                )
            },5000)
        }
    }
}
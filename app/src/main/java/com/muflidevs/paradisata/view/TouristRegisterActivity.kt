package com.muflidevs.paradisata.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.ActivityTouristRegisterBinding

class TouristRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTouristRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTouristRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
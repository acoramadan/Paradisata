package com.muflidevs.paradisata.ui.view.tourguide

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigation.NavigationBarView
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.databinding.ActivityTourGuideMainBinding
import com.muflidevs.paradisata.ui.view.fragments.FragmentOrderStatus
import com.muflidevs.paradisata.ui.view.fragments.HistoryFragment
import com.muflidevs.paradisata.ui.view.fragments.HomeFragment
import com.muflidevs.paradisata.ui.view.fragments.ProfileFragment
import com.muflidevs.paradisata.ui.view.fragments.TourGuideFragment

class TourGuideMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTourGuideMainBinding
    private lateinit var navBar: NavigationBarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTourGuideMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navBarClick()
    }
    private fun navBarClick() {
        navBar = binding.bottomNavigation

        val fragmentManager = supportFragmentManager
        val homeFragment = TourGuideFragment()
        val historyFragment = FragmentOrderStatus()
        val profileFragment = ProfileFragment()

        fragmentManager.beginTransaction()
            .replace(binding.frameContainer.id, homeFragment, TourGuideFragment::class.java.simpleName)
            .commit()

        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    fragmentManager.beginTransaction()
                        .replace(
                            binding.frameContainer.id,
                            homeFragment,
                            TourGuideFragment::class.java.simpleName
                        )
                        .commit()
                    true
                }

                R.id.nav_history -> {
                    fragmentManager.beginTransaction()
                        .replace(
                            binding.frameContainer.id,
                            historyFragment,
                            FragmentOrderStatus::class.java.simpleName
                        )
                        .commit()
                    true
                }

                R.id.nav_profile -> {
                    fragmentManager.beginTransaction()
                        .replace(
                            binding.frameContainer.id,
                            profileFragment,
                            ProfileFragment::class.java.simpleName
                        )
                        .commit()
                    true
                }

                else -> false
            }
        }
    }
}
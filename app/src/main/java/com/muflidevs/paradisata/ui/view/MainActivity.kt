package com.muflidevs.paradisata.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationBarView
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import com.muflidevs.paradisata.databinding.ActivityMainBinding
import com.muflidevs.paradisata.ui.view.category.CategoryCulinaryActivity
import com.muflidevs.paradisata.ui.view.category.CategoryHistoryActivity
import com.muflidevs.paradisata.ui.view.category.CategoryNatureActivity
import com.muflidevs.paradisata.ui.view.category.CategoryReligionActivity
import com.muflidevs.paradisata.ui.view.fragments.HomeFragment
import com.muflidevs.paradisata.ui.view.fragments.HistoryFragment
import com.muflidevs.paradisata.ui.view.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navBar: NavigationBarView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navBarClick()
    }


    private fun navBarClick() {
        navBar = binding.bottomNavigation

        val fragmentManager = supportFragmentManager
        val homeFragment = HomeFragment()
        val historyFragment = HistoryFragment()
        val profileFragment = ProfileFragment()

        fragmentManager.beginTransaction()
            .replace(binding.frameContainer.id, homeFragment, HomeFragment::class.java.simpleName)
            .commit()

        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    fragmentManager.beginTransaction()
                        .replace(
                            binding.frameContainer.id,
                            homeFragment,
                            HomeFragment::class.java.simpleName
                        )
                        .commit()
                    true
                }

                R.id.nav_history -> {
                    fragmentManager.beginTransaction()
                        .replace(
                            binding.frameContainer.id,
                            historyFragment,
                            HistoryFragment::class.java.simpleName
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
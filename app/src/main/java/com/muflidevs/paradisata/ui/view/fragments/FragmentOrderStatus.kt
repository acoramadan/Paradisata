package com.muflidevs.paradisata.ui.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.replace
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.FragmentOrderStatusBinding

class FragmentOrderStatus : Fragment() {
    private lateinit var binding: FragmentOrderStatusBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navBar()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderStatusBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    private fun navBar() {
        val toAcceptFragment = FragmentTourGuideToAccept()
        val toCompletedFragment = FragmentTourGuideToCompleted()
        childFragmentManager.beginTransaction()
            .replace(binding.frameContainer.id,toAcceptFragment)
            .commit()
        binding.btnToAccept.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(binding.frameContainer.id,toAcceptFragment)
                .commit()

            binding.btnCompleted.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.md_theme_onSecondary
                )
            )
            binding.btnCompleted.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.md_theme_onBackground
                )
            )
            binding.btnToAccept.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.md_theme_secondaryContainer
                )
            )
            binding.btnToAccept.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.md_theme_onSecondary
                )
            )
        }
        binding.btnCompleted.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(binding.frameContainer.id,toCompletedFragment)
                .commit()

            binding.btnCompleted.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.md_theme_secondaryContainer
                )
            )
            binding.btnToAccept.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.md_theme_onSecondary
                )
            )
            binding.btnToAccept.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.md_theme_onSecondary
                )
            )
            binding.btnToAccept.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.md_theme_onBackground
                )
            )
        }
    }

}
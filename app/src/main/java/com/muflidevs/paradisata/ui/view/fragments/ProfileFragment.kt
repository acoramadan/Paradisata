package com.muflidevs.paradisata.ui.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.FragmentProfileBinding
import com.muflidevs.paradisata.ui.view.AppInfoActivity
import com.muflidevs.paradisata.ui.view.EditProfileActivity
import com.muflidevs.paradisata.ui.view.LoginActivity
import com.muflidevs.paradisata.ui.view.MainActivity
import com.muflidevs.paradisata.ui.view.tourguide.TourGuideMainActivity
import com.muflidevs.paradisata.viewModel.LoginViewModel
import com.muflidevs.paradisata.viewModel.TourGuideViewModel
import com.muflidevs.paradisata.viewModel.TouristViewModel
import com.muflidevs.paradisata.viewModel.UserViewModel
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: LoginViewModel by viewModels()
    private val tourist: TouristViewModel by viewModels()
    private val tourGuideModel: TourGuideViewModel by viewModels()
    private val userModel : UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        with(binding) {
            logout.setOnClickListener {
                signOut()
            }
            appInfo.setOnClickListener {
                startActivity(Intent(requireContext(),AppInfoActivity::class.java))
            }
            editProfile.setOnClickListener {
                startActivity(Intent(requireContext(),EditProfileActivity::class.java))
            }
        }
        userModel.getUser(userModel.getUserToken())
        userModel.user.observe(requireActivity()) { user ->
            binding.userName.text = user?.userName
        }
        if(activity is TourGuideMainActivity) {
            tourGuideModel.getTourGuide(userModel.getUserToken())
            tourGuideModel.aTourGuide.observe(requireActivity()) { tourGuide ->
                Log.d("Profile Fragment " , "Data TourGuide : $tourGuide")
                Glide.with(requireActivity())
                    .load(tourGuide?.photo)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.profileImage)
            }
        } else {
            tourist.getTourist(userModel.getUserToken())
            tourist.tourist.observe(requireActivity()) {tourist ->
                Log.d("Profile Fragment " , "Data Tourist : $tourist")
                Glide.with(requireActivity())
                    .load(tourist?.photo)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.profileImage)
            }
        }
    }

    private fun signOut() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(requireContext())
            auth.signOut()
            viewModel.logout()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            startActivity(Intent(requireContext(),LoginActivity::class.java))
            activity?.finish()
        }

    }

}
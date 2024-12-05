package com.muflidevs.paradisata.ui.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.databinding.FragmentProfileBinding
import com.muflidevs.paradisata.ui.view.AppInfoActivity
import com.muflidevs.paradisata.ui.view.EditProfileActivity
import com.muflidevs.paradisata.ui.view.LoginActivity
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding
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

    }

    private fun signOut() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(requireContext())
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            startActivity(Intent(requireContext(),LoginActivity::class.java))
            activity?.finish()
        }

    }

}
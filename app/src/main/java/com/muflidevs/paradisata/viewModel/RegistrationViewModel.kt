package com.muflidevs.paradisata.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.muflidevs.paradisata.data.model.remote.registration.TourGuide
import com.muflidevs.paradisata.data.model.remote.registration.Tourist


class RegistrationViewModel(application: Application): AndroidViewModel(application) {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _tourist = MutableLiveData<Tourist>()
    val tourist: LiveData<Tourist> get() = _tourist

    private val _tourGuide = MutableLiveData<TourGuide>()
    val tourGuide: LiveData<TourGuide> get() = _tourGuide

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun registerNewUser
}
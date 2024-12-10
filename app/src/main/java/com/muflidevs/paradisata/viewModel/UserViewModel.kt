package com.muflidevs.paradisata.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance("db-user")

    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> get() = _userRole

    fun checkUserRole(userId: String) {
        val userDocRef = db.collection("user").document(userId)
        userDocRef.collection("tourGuide").get()
            .addOnSuccessListener { tourguideSnapshot ->
                if (!tourguideSnapshot.isEmpty) {
                    _userRole.value = "tourGwuide"
                } else {
                    userDocRef.collection("tourist").get()
                        .addOnSuccessListener { touristSnapshot ->
                            if (!touristSnapshot.isEmpty) {
                                _userRole.value = "tourist"
                            } else {
                                _userRole.value = "none"
                            }
                        }
                }
            }
    }
}

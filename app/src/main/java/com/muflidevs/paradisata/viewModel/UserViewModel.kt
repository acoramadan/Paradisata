package com.muflidevs.paradisata.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.muflidevs.paradisata.data.model.remote.registration.User

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val db = FirebaseFirestore.getInstance("db-user")

    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> get() = _userRole

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    fun checkUserRole(userId: String) {
        val userDocRef = db.collection("user").document(userId)
        userDocRef.collection("tourGuide").get()
            .addOnSuccessListener { tourguideSnapshot ->
                if (!tourguideSnapshot.isEmpty) {
                    _userRole.value = "tourGuide"
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

    fun getUser(userId: String) {
        val userDocRef = db.collection("user").document(userId)
        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(User::class.java)

                    user?.let {
                        _user.postValue(user)
                    } ?: run {
                        Log.d("User", "User data is null")
                    }
                } else {
                    Log.d("User", "No such user exists!")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("User", "Error getting user data", exception)
            }
    }

    fun getUserToken(): String {
        val sharedPref = getApplication<Application>().getSharedPreferences(
            "app_preferences",
            Context.MODE_PRIVATE
        )
        return sharedPref.getString("user_token", null) ?: " "
    }
}

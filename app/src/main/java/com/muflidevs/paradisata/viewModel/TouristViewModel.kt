package com.muflidevs.paradisata.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.muflidevs.paradisata.data.model.remote.registration.Tourist

class TouristViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance("db-user")

    private val _tourist = MutableLiveData<Tourist?>()
    val tourist: LiveData<Tourist?> get() = _tourist

    fun getTourist(userId: String) {
        val touristDocRef = db.collection("user").document(userId)
            .collection("tourist")
        Log.d("Tourist", "Token : $userId")
        touristDocRef.get()
            .addOnSuccessListener { documentSnapShot ->
                if (!documentSnapShot.isEmpty) {
                    for (document in documentSnapShot) {
                        val tourist = document.toObject(Tourist::class.java)
                        tourist.let {
                            _tourist.postValue(tourist)
                        }
                    }
                } else {
                    Log.d("Tourist", "No such Tourist exists!")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Tourist", "Error getting Tourist data", exception)
            }

    }
}


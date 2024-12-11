package com.muflidevs.paradisata.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import com.muflidevs.paradisata.data.model.remote.json.TouristRating
import com.muflidevs.paradisata.data.model.remote.registration.Tourist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class TourGuideViewModel(application: Application) : AndroidViewModel(application) {

    private val _tourGuides = MutableLiveData<List<TourGuide>>()
    var tourGuide: LiveData<List<TourGuide>> = _tourGuides

    private val _tourGuide = MutableLiveData<com.muflidevs.paradisata.data.model.remote.registration.TourGuide>()
    var aTourGuide: LiveData<com.muflidevs.paradisata.data.model.remote.registration.TourGuide> = _tourGuide
    private val db = FirebaseFirestore.getInstance("db-user")


    private val _isLoading = MutableLiveData<Boolean>()
    var isLoading: LiveData<Boolean> = _isLoading

    private val _tourGuidesRating = MutableLiveData<List<TouristRating>>()
    var tourGuidesRating: LiveData<List<TouristRating>> = _tourGuidesRating
    private lateinit var dataTur: List<TourGuide>
    fun fetchTourGuide(limit: Int = 0) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if(limit != 0) {
                    dataTur = readPlacesFromRaw().take(limit)
                } else {
                    dataTur = readPlacesFromRaw()
                }

                Log.d("TourGuideViewModel", "Data yang dikirim : $dataTur")
                _tourGuides.postValue(dataTur)

            } catch (e: Exception) {
                Log.e("TourGuideViewModel", "${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fecthTourGuideRating() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = readPlacesFromRawRating()
                Log.d("TourGuideViewModel", "Data yang dikirim : $data")
                _tourGuidesRating.postValue(data)

            } catch (e: Exception) {
                Log.e("TourGuideViewModel", "${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private  suspend fun readPlacesFromRaw(): List<TourGuide> {
        return withContext(Dispatchers.IO) {
            val raw = getApplication<Application>().resources.openRawResource(R.raw.tour_guide)
            val reader = InputStreamReader(raw)
            val bufferedReader = BufferedReader(reader)
            try {
                val json = bufferedReader.readText()
                val tourGuides = Gson().fromJson(json, Array<TourGuide>::class.java).toList()
                tourGuides
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            } finally {
                bufferedReader.close()
            }
        }
    }

    private suspend fun readPlacesFromRawRating(): List<TouristRating> {
        return withContext(Dispatchers.IO) {
            val raw = getApplication<Application>().resources.openRawResource(R.raw.guide_ratings_reviews)
            val reader = InputStreamReader(raw)
            val bufferedReader = BufferedReader(reader)
            try {
                val json = bufferedReader.readText()
                val tourGuides = Gson().fromJson(json, Array<TouristRating>::class.java).toList()
                tourGuides.take(10)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            } finally {
                bufferedReader.close()
            }
        }
    }
    fun getTourGuide(userId: String) {
        val touristDocRef = db.collection("user").document(userId)
            .collection("tourGuide")
        Log.d("TourGuide", "Token : $userId")
        touristDocRef.get()
            .addOnSuccessListener { documentSnapShot ->
                if (!documentSnapShot.isEmpty) {
                    for(document in documentSnapShot) {
                        val tourGuide = document.toObject(com.muflidevs.paradisata.data.model.remote.registration.TourGuide::class.java)
                        tourGuide.let {
                            _tourGuide.postValue(tourGuide)
                        }
                    }
                } else {
                    Log.d("TourGuide", "No such TourGuide exists!")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TourGuide", "Error getting TourGuide data", exception)
            }

    }


}
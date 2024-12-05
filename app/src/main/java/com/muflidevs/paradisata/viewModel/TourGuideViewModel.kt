package com.muflidevs.paradisata.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.muflidevs.paradisata.R
import com.muflidevs.paradisata.data.model.remote.json.TourGuide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class TourGuideViewModel(application: Application) : AndroidViewModel(application) {

    private val _tourGuides = MutableLiveData<List<TourGuide>>()
    var tourGuide: LiveData<List<TourGuide>> = _tourGuides

    private val _isLoading = MutableLiveData<Boolean>()
    var isLoading: LiveData<Boolean> = _isLoading

    fun fetchTourGuide() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val data = readPlacesFromRaw()
                Log.d("TourGuideViewModel", "Data yang dikirim : $data")
                _tourGuides.postValue(data)

            } catch (e: Exception) {
                Log.e("TourGuideViewModel", "${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun readPlacesFromRaw(): List<TourGuide> {
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

}
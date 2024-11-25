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
import com.muflidevs.paradisata.data.model.remote.json.DataPlaces
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class PlaceViewModel(application: Application): AndroidViewModel(application) {
    private val _places = MutableLiveData<List<DataPlaces>>()
    val places: LiveData<List<DataPlaces>> get() = _places

    fun loadPlaces() {
        viewModelScope.launch {
            val data = readPlacesFromRaw()
            _places.postValue(data)
        }
    }

    private suspend fun readPlacesFromRaw(): List<DataPlaces> {
        return withContext(Dispatchers.IO) {
            val raw = getApplication<Application>().resources.openRawResource(R.raw.places_by_city)
            val reader = InputStreamReader(raw)
            val type = object : TypeToken<List<DataPlaces>>() {}.type
            Gson().fromJson<List<DataPlaces>>(reader, type)
        }
    }
}
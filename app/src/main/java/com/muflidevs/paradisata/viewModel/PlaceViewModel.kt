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

class PlaceViewModel(application: Application) : AndroidViewModel(application) {
    private val _places = MutableLiveData<List<DataPlaces>>()
    val places: LiveData<List<DataPlaces>> get() = _places

    fun loadPlaces(limit: Int = 5) {
        viewModelScope.launch {
            try {
                val data = readPlacesFromRaw(limit)
                 _places.postValue(data)
            } catch (e: Exception) {
                Log.e("PlaceViewModel", "${e.message}")
            }

        }
    }

    private suspend fun readPlacesFromRaw(limit: Int = 0): List<DataPlaces> {
        return withContext(Dispatchers.IO) {
            val raw = getApplication<Application>().resources.openRawResource(R.raw.places_by_city)
            val reader = InputStreamReader(raw)
            try {
                val jsonArray = Gson().fromJson<List<Map<String, Any>>>(
                    reader,
                    object : TypeToken<List<Map<String, Any>>>() {}.type
                )

                val limitedJson = if(limit > 1) jsonArray.take(limit) else jsonArray

                val limitedPlaces = limitedJson.mapNotNull { item ->
                    try {
                        Gson().fromJson(Gson().toJson(item), DataPlaces::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
                limitedPlaces
            } catch (e: Exception) {
                emptyList()
            } finally {
                reader.close()
            }
        }
    }
}
package com.muflidevs.paradisata.viewModel.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.muflidevs.paradisata.viewModel.TourGuideViewModel

class TourGuideViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TourGuideViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TourGuideViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

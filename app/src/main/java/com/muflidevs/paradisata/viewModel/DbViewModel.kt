package com.muflidevs.paradisata.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muflidevs.paradisata.data.model.remote.db.UserInteraction
import com.muflidevs.paradisata.data.model.remote.db.UserInteractionRepo
import kotlinx.coroutines.launch

class DbViewModel(application : Application) : ViewModel() {
    private val mRepo = UserInteractionRepo(application)

    fun insert(userInteraction: UserInteraction) {
        viewModelScope.launch {
            val user = mRepo.mapRemoteToLocal(userInteraction)
            mRepo.insert(user)
        }
    }
}
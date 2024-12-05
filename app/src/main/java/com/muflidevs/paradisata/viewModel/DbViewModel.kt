package com.muflidevs.paradisata.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muflidevs.paradisata.data.model.remote.db.UserInteraction
import com.muflidevs.paradisata.data.model.remote.db.UserInteractionRepo
import kotlinx.coroutines.launch

class DbViewModel(application : Application) : AndroidViewModel(application) {
    private val mRepo = UserInteractionRepo(application)

    fun getAllUserInteractions(): LiveData<List<UserInteraction>> {
        return mRepo.getAllInteraction()
    }
    fun insert(userInteraction: UserInteraction) {
        viewModelScope.launch {
            val user = mRepo.mapRemoteToLocal(userInteraction)
            mRepo.insert(user)
        }
    }
}
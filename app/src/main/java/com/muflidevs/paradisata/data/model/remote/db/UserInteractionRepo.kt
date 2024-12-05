package com.muflidevs.paradisata.data.model.remote.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserInteractionRepo(application: Application) {
    private val mUserDao: UserInteractionDAO

    init {
        val db = UserInteracationDB.getDatabase(application)
        mUserDao = db.UserInteractionDAO()
    }
    fun getAllInteraction():LiveData<List<UserInteraction>> = mUserDao.getAllInteraction()

    suspend fun insert(userInteraction: UserInteraction) {
        withContext(Dispatchers.IO) {
            mUserDao.insert(userInteraction)
        }
    }

    fun mapRemoteToLocal(userInteraction: UserInteraction): UserInteraction {
        return UserInteraction(
            activites = userInteraction.activites,
            kategori = userInteraction.kategori,
            rating = userInteraction.rating
        )
    }
}
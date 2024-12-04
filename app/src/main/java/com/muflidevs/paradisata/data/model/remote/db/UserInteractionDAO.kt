package com.muflidevs.paradisata.data.model.remote.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserInteractionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userInteraction: UserInteraction)

    @Query("SELECT * FROM user_interaction")
    fun getAllInteraction(): LiveData<List<UserInteraction>>
}
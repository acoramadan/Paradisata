package com.muflidevs.paradisata.data.model.remote.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface UserInteractionDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userInteraction: UserInteraction)
}
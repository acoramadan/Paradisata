package com.muflidevs.paradisata.data.model.remote.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UserInteraction::class], version = 1)
@TypeConverters(Converters::class)
abstract class UserInteracationDB: RoomDatabase() {
    abstract fun UserInteractionDAO(): UserInteractionDAO

    companion object {
        @Volatile
        private var INSTANCE: UserInteracationDB? = null

        @JvmStatic
        fun getDatabase(context: Context): UserInteracationDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserInteracationDB::class.java, "user_interaction_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
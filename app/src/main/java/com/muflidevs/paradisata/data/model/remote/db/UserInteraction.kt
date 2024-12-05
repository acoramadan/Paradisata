package com.muflidevs.paradisata.data.model.remote.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity("user_interaction")
@TypeConverters(Converters::class)
data class UserInteraction (
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null,

    @ColumnInfo(name = "activities")
    var activites: List<String>,

    @ColumnInfo(name = "kategori")
    var kategori: String,

    @ColumnInfo(name = "rating")
    var rating: Float
)
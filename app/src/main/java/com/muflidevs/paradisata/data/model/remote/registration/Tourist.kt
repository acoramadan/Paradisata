package com.muflidevs.paradisata.data.model.remote.registration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tourist(
    var email:String,
    var phoneNumber: String,
    var userName: String,
    var password: String,
    var fullName: String,
    var address: String,
    var gender: String,
    var touristFrom: String,
    var photo: String
):Parcelable
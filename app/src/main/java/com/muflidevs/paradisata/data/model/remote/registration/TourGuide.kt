package com.muflidevs.paradisata.data.model.remote.registration

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class TourGuide(
    var id: String = "",
    var fullName: String= "",
    var address: String= "",
    var gender: String = "",
    var dateOfBirth: String = "",
    var photo: String = "",
):Parcelable

@Parcelize
data class PackageName(
    var id: String = "",
    var name: String = "",
    var homeStayPhoto: String = "",
    var address: String = "",
    var facilities: List<String>,
    var transportationPhoto: String = "",
    var transportationType: String = "",
    var totalGuest: Int
) : Parcelable
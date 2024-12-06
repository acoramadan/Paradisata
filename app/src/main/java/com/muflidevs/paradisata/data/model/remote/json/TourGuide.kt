package com.muflidevs.paradisata.data.model.remote.json

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TourGuide(
    @SerializedName("GuideID")
    val id: Int,

    @SerializedName("Name")
    val name: String,

    @SerializedName("Address")
    val address: String,

    @SerializedName("Gender")
    val gender: String,

    @SerializedName("ContactInfo")
    val contactInfo: String,

    @SerializedName("ProfilePicture")
    val profilePicture: String,

    @SerializedName("About")
    val about: String,

    @SerializedName("PackageName")
    val packageName: String,

    @SerializedName("HomestayPicture")
    val homestay: String,

    @SerializedName("HomestayAddress")
    val homestayAddress: String,

    @SerializedName("Facilities")
    val facilities: List<String>,

    @SerializedName("TotalGuest")
    val totalGuest: Int,

    @SerializedName("TransportationPicture")
    val transportationPicture: String,

    @SerializedName("TransportationType")
    val transportationType: String,

    @SerializedName("Prize")
    val prize: Int,

    @SerializedName("Rating")
    val rating: Float,

    @SerializedName("JumlahUlasan")
    val jumlahUlasan: Int,

    var touristRating: List<TouristRating>
): Parcelable

@Parcelize
data class TouristRating(
    @SerializedName("UserID")
    val idUser: Int,

    @SerializedName("GuideID")
    val idTourGuide: Int,

    @SerializedName("Rating")
    val rating: Float,

    @SerializedName("Ulasan")
    val ulasan: String
): Parcelable
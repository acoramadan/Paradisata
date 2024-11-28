package com.muflidevs.paradisata.data.model.remote.json

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class DataPlaces(
    @SerializedName("id_place")
    val id: Int,
    val kategori: String,
    val nama: String,
    val foto: List<String>,
    val tentang: String,
    val alamat: String,
    val fasilitas: List<String> = emptyList(),
    val aktivitas: List<String> = emptyList(),
    val rating: Float,
    @SerializedName("ulasan")
    val ulasanList: List<UlasanPlaces> = emptyList(),
    val harga: String,
    @SerializedName("jam_buka")
    val jamBuka: String
):Parcelable

@Parcelize
data class UlasanPlaces(
    val user: String,
    val komentar: String,
    val rating: Float,
):Parcelable
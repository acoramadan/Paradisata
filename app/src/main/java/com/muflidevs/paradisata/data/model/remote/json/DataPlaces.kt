package com.muflidevs.paradisata.data.model.remote.json

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class DataPlaces(
    val kategori: String,
    val nama: String,
    val foto: List<String>,
    val tentang: String,
    val alamat: String,
    val fasilitas: List<String>,
    val aktivitas: List<String>,
    val rating: Float,
    val ulasanList: @RawValue List<UlasanPlaces>,
    val harga: String,
    val jamBuka: String
):Parcelable

data class UlasanPlaces(
    val user: String,
    val komentar: String,
    val rating: Float,
)
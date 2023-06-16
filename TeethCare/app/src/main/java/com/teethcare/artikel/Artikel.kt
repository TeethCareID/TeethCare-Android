package com.teethcare.artikel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artikel(
    val id: Int,
    val imageArtikel: Int,
    val judulArtikel: String,
    val kategoriArtikel: String,
    val tanggalArtikel: String,
    val kontenArtikel: String
): Parcelable
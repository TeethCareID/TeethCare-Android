package com.teethcare.model

data class DataDiri(
    val nama: String? = null,
    val noHp: String? = null,
    val doB: String? = null,
    val jenisKelamin: String? = null,
    val alamat: String? = null
) {
    constructor():this("","","","","")
}

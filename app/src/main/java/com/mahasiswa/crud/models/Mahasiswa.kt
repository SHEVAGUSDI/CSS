package com.mahasiswa.crud.models

data class Mahasiswa(
    val id: Int = 0,
    val nim: String,
    val nama: String,
    val alamat: String,
    val telepon: String,
    val email: String,
    val jurusanId: Int,
    var jurusanNama: String = "" // For display purposes
)
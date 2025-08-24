package com.mahasiswa.crud.models

data class Dosen(
    val id: Int = 0,
    val nip: String,
    val namaDosen: String,
    val alamat: String,
    val telepon: String,
    val email: String
)
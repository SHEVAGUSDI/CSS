package com.student.crud.models

data class Mahasiswa(
    val id: Int = 0,
    val nim: String,
    val nama: String,
    val alamat: String,
    val telepon: String,
    val email: String,
    val jurusanId: Int,
    val jurusanNama: String = "" // For display purposes
)
package com.student.crud.models

data class MataKuliah(
    val id: Int = 0,
    val kodeMk: String,
    val namaMk: String,
    val sks: Int,
    val semester: Int,
    val dosenId: Int,
    val dosenNama: String = "" // For display purposes
)
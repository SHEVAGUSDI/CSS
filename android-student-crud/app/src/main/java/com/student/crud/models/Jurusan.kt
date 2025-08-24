package com.student.crud.models

data class Jurusan(
    val id: Long = 0,
    val kodeJurusan: String,
    val namaJurusan: String,
    val fakultas: String
) {
    fun isValid(): Boolean {
        return kodeJurusan.isNotBlank() && 
               namaJurusan.isNotBlank() && 
               fakultas.isNotBlank()
    }
}
package com.mahasiswa.crud.models

data class Nilai(
    val id: Int = 0,
    val mahasiswaId: Int,
    val mataKuliahId: Int,
    val nilai: Double,
    val grade: String,
    val tanggalInput: String,
    var mahasiswaNama: String = "", // For display purposes
    var mataKuliahNama: String = "" // For display purposes
) {
    companion object {
        fun calculateGrade(nilai: Double): String {
            return when {
                nilai >= 85 -> "A"
                nilai >= 70 -> "B"
                nilai >= 55 -> "C"
                nilai >= 40 -> "D"
                else -> "E"
            }
        }
    }
}
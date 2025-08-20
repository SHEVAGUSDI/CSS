package com.student.crud.models

data class Nilai(
    val id: Int = 0,
    val mahasiswaId: Int,
    val mataKuliahId: Int,
    val nilai: Double,
    val grade: String,
    val tanggalInput: String,
    val mahasiswaNama: String = "", // For display purposes
    val mataKuliahNama: String = ""  // For display purposes
) {
    companion object {
        fun calculateGrade(nilai: Double): String {
            return when {
                nilai >= 85 -> "A"
                nilai >= 70 -> "B"
                nilai >= 60 -> "C"
                nilai >= 50 -> "D"
                else -> "E"
            }
        }
    }
}
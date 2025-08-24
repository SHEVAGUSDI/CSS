package com.student.crud.models

data class Nilai(
    val id: Long = 0,
    val mahasiswaId: Long,
    val mataKuliahId: Long,
    val nilai: Double,
    val grade: String,
    val tanggalInput: String
) {
    fun isValid(): Boolean {
        return mahasiswaId > 0 && 
               mataKuliahId > 0 && 
               nilai >= 0.0 && nilai <= 100.0 && 
               grade.isNotBlank() && 
               tanggalInput.isNotBlank()
    }
    
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
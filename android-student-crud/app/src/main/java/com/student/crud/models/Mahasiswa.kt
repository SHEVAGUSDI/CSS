package com.student.crud.models

data class Mahasiswa(
    val id: Long = 0,
    val nim: String,
    val nama: String,
    val alamat: String,
    val telepon: String,
    val email: String,
    val jurusanId: Long
) {
    // Validation methods
    fun isValid(): Boolean {
        return nim.isNotBlank() && 
               nama.isNotBlank() && 
               alamat.isNotBlank() && 
               telepon.isNotBlank() && 
               isValidEmail(email) && 
               jurusanId > 0
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
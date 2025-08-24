package com.student.crud.models

data class Dosen(
    val id: Long = 0,
    val nip: String,
    val namaDosen: String,
    val alamat: String,
    val telepon: String,
    val email: String
) {
    fun isValid(): Boolean {
        return nip.isNotBlank() && 
               namaDosen.isNotBlank() && 
               alamat.isNotBlank() && 
               telepon.isNotBlank() && 
               isValidEmail(email)
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
package com.student.crud.models

data class MataKuliah(
    val id: Long = 0,
    val kodeMk: String,
    val namaMk: String,
    val sks: Int,
    val semester: Int,
    val dosenId: Long
) {
    fun isValid(): Boolean {
        return kodeMk.isNotBlank() && 
               namaMk.isNotBlank() && 
               sks > 0 && 
               semester > 0 && 
               dosenId > 0
    }
}
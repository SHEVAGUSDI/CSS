package com.student.crud

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.student.crud.adapters.JurusanAdapter
import com.student.crud.database.DatabaseHelper
import com.student.crud.models.Jurusan

class JurusanActivity : AppCompatActivity() {
    
    private lateinit var etKodeJurusan: TextInputEditText
    private lateinit var etNamaJurusan: TextInputEditText
    private lateinit var etFakultas: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnClear: Button
    private lateinit var recyclerView: RecyclerView
    
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var jurusanAdapter: JurusanAdapter
    private var currentJurusan: Jurusan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jurusan)
        
        supportActionBar?.title = "Data Jurusan"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        initViews()
        setupDatabase()
        setupRecyclerView()
        setupClickListeners()
        loadData()
    }
    
    private fun initViews() {
        etKodeJurusan = findViewById(R.id.etKodeJurusan)
        etNamaJurusan = findViewById(R.id.etNamaJurusan)
        etFakultas = findViewById(R.id.etFakultas)
        btnSave = findViewById(R.id.btnSave)
        btnClear = findViewById(R.id.btnClear)
        recyclerView = findViewById(R.id.recyclerView)
    }
    
    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(this)
    }
    
    private fun setupRecyclerView() {
        jurusanAdapter = JurusanAdapter(
            mutableListOf(),
            onEditClick = { jurusan ->
                currentJurusan = jurusan
                populateForm(jurusan)
            },
            onDeleteClick = { jurusan ->
                showDeleteConfirmation(jurusan)
            }
        )
        
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@JurusanActivity)
            adapter = jurusanAdapter
        }
    }
    
    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            if (validateInput()) {
                saveJurusan()
            }
        }
        
        btnClear.setOnClickListener {
            clearForm()
        }
    }
    
    private fun validateInput(): Boolean {
        val kodeJurusan = etKodeJurusan.text.toString().trim()
        val namaJurusan = etNamaJurusan.text.toString().trim()
        val fakultas = etFakultas.text.toString().trim()
        
        when {
            kodeJurusan.isEmpty() -> {
                etKodeJurusan.error = "Kode jurusan tidak boleh kosong"
                etKodeJurusan.requestFocus()
                return false
            }
            namaJurusan.isEmpty() -> {
                etNamaJurusan.error = "Nama jurusan tidak boleh kosong"
                etNamaJurusan.requestFocus()
                return false
            }
            fakultas.isEmpty() -> {
                etFakultas.error = "Fakultas tidak boleh kosong"
                etFakultas.requestFocus()
                return false
            }
        }
        return true
    }
    
    private fun saveJurusan() {
        val kodeJurusan = etKodeJurusan.text.toString().trim()
        val namaJurusan = etNamaJurusan.text.toString().trim()
        val fakultas = etFakultas.text.toString().trim()
        
        val jurusan = if (currentJurusan != null) {
            currentJurusan!!.copy(
                kodeJurusan = kodeJurusan,
                namaJurusan = namaJurusan,
                fakultas = fakultas
            )
        } else {
            Jurusan(
                kodeJurusan = kodeJurusan,
                namaJurusan = namaJurusan,
                fakultas = fakultas
            )
        }
        
        val result = if (currentJurusan != null) {
            databaseHelper.updateJurusan(jurusan)
        } else {
            databaseHelper.insertJurusan(jurusan)
        }
        
        if (result > 0) {
            val message = if (currentJurusan != null) "Data berhasil diupdate" else "Data berhasil disimpan"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            clearForm()
            loadData()
        } else {
            Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun populateForm(jurusan: Jurusan) {
        etKodeJurusan.setText(jurusan.kodeJurusan)
        etNamaJurusan.setText(jurusan.namaJurusan)
        etFakultas.setText(jurusan.fakultas)
        btnSave.text = "Update"
    }
    
    private fun clearForm() {
        etKodeJurusan.text?.clear()
        etNamaJurusan.text?.clear()
        etFakultas.text?.clear()
        currentJurusan = null
        btnSave.text = "Simpan"
        
        // Clear any errors
        etKodeJurusan.error = null
        etNamaJurusan.error = null
        etFakultas.error = null
    }
    
    private fun showDeleteConfirmation(jurusan: Jurusan) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus data jurusan \"${jurusan.namaJurusan}\"?")
            .setPositiveButton("Ya") { _, _ ->
                deleteJurusan(jurusan)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    
    private fun deleteJurusan(jurusan: Jurusan) {
        val result = databaseHelper.deleteJurusan(jurusan.id)
        if (result > 0) {
            Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
            loadData()
            if (currentJurusan?.id == jurusan.id) {
                clearForm()
            }
        } else {
            Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadData() {
        val jurusanList = databaseHelper.getAllJurusan()
        jurusanAdapter.updateData(jurusanList)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
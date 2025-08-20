package com.student.crud

import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.student.crud.adapters.MahasiswaAdapter
import com.student.crud.database.DatabaseHelper
import com.student.crud.models.Jurusan
import com.student.crud.models.Mahasiswa

class MahasiswaActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: MahasiswaAdapter
    private lateinit var recyclerView: RecyclerView
    
    private lateinit var etNim: TextInputEditText
    private lateinit var etNamaMahasiswa: TextInputEditText
    private lateinit var etAlamat: TextInputEditText
    private lateinit var etTelepon: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var spinnerJurusan: AppCompatAutoCompleteTextView
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var fabAdd: FloatingActionButton
    
    private var editingMahasiswa: Mahasiswa? = null
    private var isFormVisible = false
    private var jurusanList: List<Jurusan> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mahasiswa)

        initializeViews()
        setupDatabase()
        setupRecyclerView()
        setupListeners()
        loadJurusanData()
        loadData()
        hideForm()
    }

    private fun initializeViews() {
        etNim = findViewById(R.id.etNim)
        etNamaMahasiswa = findViewById(R.id.etNamaMahasiswa)
        etAlamat = findViewById(R.id.etAlamat)
        etTelepon = findViewById(R.id.etTelepon)
        etEmail = findViewById(R.id.etEmail)
        spinnerJurusan = findViewById(R.id.spinnerJurusan)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        fabAdd = findViewById(R.id.fabAdd)
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun setupDatabase() {
        dbHelper = DatabaseHelper(this)
    }

    private fun setupRecyclerView() {
        adapter = MahasiswaAdapter(
            mahasiswaList = emptyList(),
            onEditClick = { mahasiswa -> editMahasiswa(mahasiswa) },
            onDeleteClick = { mahasiswa -> confirmDelete(mahasiswa) }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        fabAdd.setOnClickListener { showForm() }
        btnSave.setOnClickListener { saveMahasiswa() }
        btnCancel.setOnClickListener { hideForm() }
    }

    private fun loadJurusanData() {
        jurusanList = dbHelper.getAllJurusan()
        val jurusanNames = jurusanList.map { "${it.namaJurusan} (${it.kodeJurusan})" }
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, jurusanNames)
        spinnerJurusan.setAdapter(arrayAdapter)
    }

    private fun showForm() {
        if (jurusanList.isEmpty()) {
            Toast.makeText(this, "Please add Jurusan data first", Toast.LENGTH_SHORT).show()
            return
        }
        
        isFormVisible = true
        findViewById<androidx.cardview.widget.CardView>(R.id.cardForm)?.visibility = android.view.View.VISIBLE
        clearForm()
        editingMahasiswa = null
    }

    private fun hideForm() {
        isFormVisible = false
        findViewById<androidx.cardview.widget.CardView>(R.id.cardForm)?.visibility = android.view.View.GONE
        clearForm()
        editingMahasiswa = null
    }

    private fun clearForm() {
        etNim.setText("")
        etNamaMahasiswa.setText("")
        etAlamat.setText("")
        etTelepon.setText("")
        etEmail.setText("")
        spinnerJurusan.setText("")
    }

    private fun editMahasiswa(mahasiswa: Mahasiswa) {
        editingMahasiswa = mahasiswa
        etNim.setText(mahasiswa.nim)
        etNamaMahasiswa.setText(mahasiswa.nama)
        etAlamat.setText(mahasiswa.alamat)
        etTelepon.setText(mahasiswa.telepon)
        etEmail.setText(mahasiswa.email)
        
        // Set spinner selection
        val selectedJurusan = jurusanList.find { it.id == mahasiswa.jurusanId }
        selectedJurusan?.let {
            spinnerJurusan.setText("${it.namaJurusan} (${it.kodeJurusan})", false)
        }
        
        showForm()
    }

    private fun saveMahasiswa() {
        if (!validateInput()) return

        val nim = etNim.text.toString().trim()
        val namaMahasiswa = etNamaMahasiswa.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val telepon = etTelepon.text.toString().trim()
        val email = etEmail.text.toString().trim()
        
        val selectedJurusanText = spinnerJurusan.text.toString()
        val selectedJurusan = jurusanList.find { "${it.namaJurusan} (${it.kodeJurusan})" == selectedJurusanText }
        
        if (selectedJurusan == null) {
            Toast.makeText(this, "Please select a valid Jurusan", Toast.LENGTH_SHORT).show()
            return
        }

        val mahasiswa = Mahasiswa(
            id = editingMahasiswa?.id ?: 0,
            nim = nim,
            nama = namaMahasiswa,
            alamat = alamat,
            telepon = telepon,
            email = email,
            jurusanId = selectedJurusan.id
        )

        try {
            if (editingMahasiswa == null) {
                // Add new mahasiswa
                val result = dbHelper.addMahasiswa(mahasiswa)
                if (result != -1L) {
                    Toast.makeText(this, getString(R.string.success_saved), Toast.LENGTH_SHORT).show()
                    hideForm()
                    loadData()
                } else {
                    Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
                }
            } else {
                // Update existing mahasiswa
                val result = dbHelper.updateMahasiswa(mahasiswa)
                if (result > 0) {
                    Toast.makeText(this, getString(R.string.success_saved), Toast.LENGTH_SHORT).show()
                    hideForm()
                    loadData()
                } else {
                    Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInput(): Boolean {
        val nim = etNim.text.toString().trim()
        val namaMahasiswa = etNamaMahasiswa.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val telepon = etTelepon.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val selectedJurusanText = spinnerJurusan.text.toString()

        if (nim.isEmpty()) {
            etNim.error = getString(R.string.field_required)
            return false
        }

        if (namaMahasiswa.isEmpty()) {
            etNamaMahasiswa.error = getString(R.string.field_required)
            return false
        }

        if (alamat.isEmpty()) {
            etAlamat.error = getString(R.string.field_required)
            return false
        }

        if (telepon.isEmpty()) {
            etTelepon.error = getString(R.string.field_required)
            return false
        }

        if (email.isEmpty()) {
            etEmail.error = getString(R.string.field_required)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.invalid_email)
            return false
        }

        if (selectedJurusanText.isEmpty()) {
            Toast.makeText(this, "Please select a Jurusan", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if NIM already exists
        val excludeId = editingMahasiswa?.id ?: -1
        if (dbHelper.isNimExists(nim, excludeId)) {
            etNim.error = getString(R.string.unique_constraint)
            return false
        }

        return true
    }

    private fun confirmDelete(mahasiswa: Mahasiswa) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteMahasiswa(mahasiswa)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun deleteMahasiswa(mahasiswa: Mahasiswa) {
        try {
            val result = dbHelper.deleteMahasiswa(mahasiswa.id)
            if (result > 0) {
                Toast.makeText(this, getString(R.string.success_deleted), Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadData() {
        val mahasiswaList = dbHelper.getAllMahasiswa()
        adapter.updateData(mahasiswaList)
    }
}